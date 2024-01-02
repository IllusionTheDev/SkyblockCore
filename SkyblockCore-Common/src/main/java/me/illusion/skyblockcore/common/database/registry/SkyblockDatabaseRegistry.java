package me.illusion.skyblockcore.common.database.registry;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.config.section.WritableConfigurationSection;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;
import me.illusion.skyblockcore.common.database.cache.SkyblockCache;
import me.illusion.skyblockcore.common.database.metrics.influx.InfluxMetricsDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.storage.SkyblockStorage;
import me.illusion.skyblockcore.common.storage.cache.redis.MemorySkyblockIslandCache;
import me.illusion.skyblockcore.common.storage.cache.redis.RedisSkyblockIslandCache;
import me.illusion.skyblockcore.common.storage.island.mongo.MongoIslandStorage;
import me.illusion.skyblockcore.common.storage.island.sql.MySQLIslandStorage;
import me.illusion.skyblockcore.common.storage.island.sql.SQLiteIslandStorage;
import me.illusion.skyblockcore.common.storage.profiles.mongo.MongoProfileStorage;
import me.illusion.skyblockcore.common.storage.profiles.sql.MySQLProfileStorage;
import me.illusion.skyblockcore.common.storage.profiles.sql.SQLiteProfileStorage;

public class SkyblockDatabaseRegistry {

    private final SkyblockDatabaseCredentialRegistry credentialRegistry = new SkyblockDatabaseCredentialRegistry();
    private final Collection<CompletableFuture<?>> futures = Sets.newConcurrentHashSet();

    private final Map<String, RegisteredDatabase> registeredDatabases = new ConcurrentHashMap<>();

    private final SkyblockPlatform platform;

    public SkyblockDatabaseRegistry(SkyblockPlatform platform) {
        this.platform = platform;

        registerDefaults();
    }

    private void registerDefaults() {
        register("island", SkyblockDatabaseProvider.of(
            new MongoIslandStorage(),
            new MySQLIslandStorage(),
            new SQLiteIslandStorage()
        ));

        register("profile", SkyblockDatabaseProvider.of(
            new MongoProfileStorage(),
            new MySQLProfileStorage(),
            new SQLiteProfileStorage()
        ));

        register("island-cache", SkyblockDatabaseProvider.of(
            new RedisSkyblockIslandCache(),
            new MemorySkyblockIslandCache()
        ));

        register("metrics", SkyblockDatabaseProvider.of(
            new InfluxMetricsDatabase()
        ));
    }

    // --- CORE LOGIC ---

    public void register(String name, SkyblockDatabaseProvider provider) {
        registeredDatabases.put(name, new RegisteredDatabase(provider, name));
        tryLoad(registeredDatabases.get(name));
    }

    public <T extends SkyblockCache> T getCache(Class<T> clazz) {
        for (RegisteredDatabase registeredDatabase : registeredDatabases.values()) {
            SkyblockDatabase database = registeredDatabase.getDatabase();

            if (clazz.isInstance(database)) {
                return clazz.cast(database);
            }
        }

        return null;
    }

    public <T extends SkyblockStorage<T>> T getStorage(Class<T> clazz) {
        for (RegisteredDatabase registeredDatabase : registeredDatabases.values()) {
            SkyblockDatabase database = registeredDatabase.getDatabase();

            if (clazz.isInstance(database) || clazz.isAssignableFrom(database.getClass())) {
                return clazz.cast(database);
            }
        }

        warn("Failed to find storage of type {0}", clazz.getSimpleName());
        return null;
    }

    public <T extends SkyblockStorage<T>> T getStorage(String name) {
        SkyblockDatabase database = getDatabase(name);

        if (database == null) {
            return null;
        }

        if (database instanceof SkyblockStorage) {
            return (T) database;
        }

        return null;
    }

    public SkyblockDatabase getDatabase(String name) {
        RegisteredDatabase registeredDatabase = registeredDatabases.get(name);

        if (registeredDatabase == null) {
            return null;
        }

        return registeredDatabase.getDatabase();
    }

    public CompletableFuture<Void> loadPossible(ConfigurationSection section) {
        loadSection(section);
        credentialRegistry.checkCyclicDependencies();

        Set<CompletableFuture<?>> temp = new HashSet<>();
        Collection<String> keys = section.getKeys();

        for (RegisteredDatabase registeredDatabase : registeredDatabases.values()) {
            if (registeredDatabase.isEnabled()) {
                continue;
            }

            if (keys.contains(registeredDatabase.getName())) {
                registeredDatabase.setAttemptedLoad(true);
            }

            temp.add(tryLoad(registeredDatabase));
        }

        return CompletableFuture.allOf(temp.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Boolean> tryLoad(RegisteredDatabase registeredDatabase) {
        if (registeredDatabase.isEnabled()) {
            warn("Database {0} is already enabled", registeredDatabase.getName());
            return CompletableFuture.completedFuture(true);
        }

        Object credentials = credentialRegistry.get(registeredDatabase.getName());

        if (credentials == null) {
            if (!registeredDatabase.hasAttemptedLoad()) { // The database credentials haven't been loaded yet
                return CompletableFuture.completedFuture(false);
            }

            warn("Database {0} has no credentials", registeredDatabase.getName());
            return CompletableFuture.completedFuture(false);
        }

        ConfigurationSection config;

        if (credentials instanceof ConfigurationSection) {
            config = (ConfigurationSection) credentials;
        } else {
            config = credentialRegistry.getCredentials((String) credentials);
        }

        if (config == null) {
            warn("Database {0} has no matching credentials", registeredDatabase.getName());

            config = registeredDatabase.getProvider().getDefaultConfiguration();

            if (config == null) {
                return CompletableFuture.completedFuture(false);
            }

            warn("Using default configuration for {0}", registeredDatabase.getName());

            if (config instanceof WritableConfigurationSection writable) {
                writable.set(registeredDatabase.getName(), config);

                warn("Wrote credentials to default configuration for {0}", registeredDatabase.getName());
                writable.save();
            }
        }

        String type = config.getString("type");

        if (type == null) {
            warn("Database {0} has no type", registeredDatabase.getName());
            return CompletableFuture.completedFuture(false);
        }

        registeredDatabase.setSpecifiedType(type);
        SkyblockDatabase database = registeredDatabase.getDatabase();

        if (database == null) {
            warn("Database {0} has no matching database", registeredDatabase.getName());
            return CompletableFuture.completedFuture(false);
        }

        return addFuture(database.enable(platform, config).thenApply(result -> {
            if (Boolean.TRUE.equals(result)) {
                registeredDatabase.setEnabled(true);
            } else {
                warn("Database {0} failed to enable", registeredDatabase.getName());
            }

            return true;
        }));

    }

    private void loadSection(ConfigurationSection section) {
        for (String key : section.getKeys()) {
            Object obj = section.get(key);

            if (obj instanceof ConfigurationSection) {
                credentialRegistry.registerCredentials(key, (ConfigurationSection) obj);
                continue;
            }

            String value = String.valueOf(obj);
            credentialRegistry.registerDependency(key, value);
        }
    }

    public boolean areAllLoaded() {
        for (RegisteredDatabase registeredDatabase : registeredDatabases.values()) {
            if (!registeredDatabase.isEnabled()) {
                warn("Database {0} is not enabled", registeredDatabase.getName());
                return false;
            }
        }

        return true;
    }

    public CompletableFuture<Boolean> finishLoading() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v -> areAllLoaded());
    }

    public CompletableFuture<Void> shutdown() {
        for (RegisteredDatabase registeredDatabase : registeredDatabases.values()) {
            if (!registeredDatabase.isEnabled()) {
                continue;
            }

            addFuture(registeredDatabase.getDatabase().flush());
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void warn(String message, Object... args) {
        platform.getLogger().log(Level.WARNING, message, args);
    }

    private <T> CompletableFuture<T> addFuture(CompletableFuture<T> future) {
        futures.add(future);
        return future.whenComplete((v, e) -> futures.remove(future));
    }
}

// Structure:
        /*
        global: # This doesn't have to be global, it can be anything
            type: mysql # Name to get on the provider
            host: localhost
            port: 3306
            database: skyblock
            username: root

        islands: global # This can be anything, as long as it is registered
        profiles: global

        bank:
            type: mongo
            host: localhost
            port: 27017
            database: skyblock
            username: root
         */

// Check for cyclic dependencies