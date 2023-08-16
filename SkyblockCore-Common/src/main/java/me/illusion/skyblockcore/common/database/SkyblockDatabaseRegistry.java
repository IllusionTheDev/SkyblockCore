package me.illusion.skyblockcore.common.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.database.cache.redis.RedisSkyblockCache;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.mongo.MongoSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.MariaDBSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.MySQLSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.PostgresSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.SQLiteSkyblockDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * This class is responsible for registering and managing all skyblock databases.
 */
public class SkyblockDatabaseRegistry {

    private final Map<String, SkyblockDatabase> databases = new ConcurrentHashMap<>();
    private final Map<Class<? extends SkyblockDatabase>, String> chosenDatabases = new ConcurrentHashMap<>();
    private final Logger logger;

    public SkyblockDatabaseRegistry(SkyblockPlatform platform) {
        this.logger = platform.getLogger();
        registerDefaultDatabases(platform);
    }

    /**
     * Registers a skyblock database
     *
     * @param database The database to register
     */
    public void register(SkyblockDatabase database) {
        databases.put(database.getName(), database);
    }

    /**
     * Gets a skyblock database by name
     *
     * @param name The name of the database
     * @return The database, or null if it does not exist
     */
    public SkyblockDatabase get(String name) {
        return databases.get(name);
    }

    /**
     * Gets the currently chosen database
     *
     * @return The chosen database
     */
    public SkyblockFetchingDatabase getChosenDatabase() {
        return getChosenDatabase(SkyblockFetchingDatabase.class);
    }

    /**
     * Gets the currently chosen database
     *
     * @return The chosen database
     */
    public SkyblockCacheDatabase getChosenCacheDatabase() {
        return getChosenDatabase(SkyblockCacheDatabase.class);
    }

    /**
     * Gets the currently chosen database
     *
     * @return The chosen database
     */
    public <DataType extends SkyblockDatabase> DataType getChosenDatabase(Class<DataType> databaseClass) {
        String name = chosenDatabases.get(databaseClass);

        if (name == null) {
            return null;
        }

        SkyblockDatabase database = get(name);

        if (database == null || !databaseClass.isAssignableFrom(database.getClass())) {
            return null;
        }

        return databaseClass.cast(database);
    }

    /**
     * Registers the default databases provided by the SkyblockCore plugin
     *
     * @param platform The platform to register the databases for
     */
    private void registerDefaultDatabases(SkyblockPlatform platform) {
        // non-sql databases
        register(new MongoSkyblockDatabase());

        // sql remote databases
        register(new MariaDBSkyblockDatabase());
        register(new MySQLSkyblockDatabase());
        register(new PostgresSkyblockDatabase());

        // sql local databases
        register(new SQLiteSkyblockDatabase(platform.getDataFolder()));

        // cache databases
        register(new RedisSkyblockCache());
    }

    /**
     * Tries to enable the preferred database, and if it fails, tries to enable the fallback
     *
     * @param setup The setup to use
     * @return A completable future that completes when the database is enabled
     */
    public CompletableFuture<Boolean> tryEnableMultiple(SkyblockDatabaseSetup<?>... setup) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        // Not a fan of this dance, but the idea is that if any of the databases fail to enable, we want to complete the future with false

        for (SkyblockDatabaseSetup<?> databaseSetup : setup) {
            futures.add(tryEnable(databaseSetup).thenApply(success -> {
                if (!success) {
                    future.complete(false);
                }

                return success;
            }));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            if (!future.isDone()) {
                future.complete(true);
            }
        });

        return future;
    }

    public <DataType extends SkyblockDatabase> CompletableFuture<Boolean> tryEnable(SkyblockDatabaseSetup<DataType> setup) {
        String preferred = setup.getPreferredDatabase();

        SkyblockDatabase database = databases.get(preferred);

        if (database == null) {
            return tryEnableFallback(setup, setup.getFallback(preferred));
        }

        return tryEnableFallback(setup, preferred);
    }

    /**
     * Tries to enable a database, and if it fails, tries to enable the fallback
     *
     * @param setup The setup to use
     * @param type  The database type to try to enable
     * @param <T>   The internal database type, such as SkyblockCacheDatabase
     * @return A completable future that completes when the database is enabled
     */
    private <T extends SkyblockDatabase> CompletableFuture<Boolean> tryEnableFallback(SkyblockDatabaseSetup<T> setup, String type) {
        if (type == null) { // If the fallback is null, we can't do anything, so we just return false
            return CompletableFuture.completedFuture(false);
        }

        SkyblockDatabase database = databases.get(type);
        String fallback = setup.getFallback(type);

        if (database == null) { // If the database specified doesn't exist, we try the fallback
            warning("Failed to find database {0}, attempting fallback..", type);
            return tryEnableFallback(setup, fallback);
        }

        Class<T> clazz = setup.getDatabaseClass();

        if (!clazz.isAssignableFrom(database.getClass()) || !setup.isSupported(clazz.cast(database))) {
            warning("Failed to enable database {0}, attempting fallback..", type);
            return tryEnableFallback(setup, fallback); // If the database is file based, and the setup doesn't support file based databases, we try the fallback
        }

        ReadOnlyConfigurationSection properties = setup.getProperties(type);

        if (properties == null) { // If there are no properties to load from, we try the fallback
            warning("Failed to find properties for {0}, attempting fallback..", type);
            return tryEnableFallback(setup, fallback);
        }

        return database.enable(properties)
            .thenCompose(success -> { // We try to enable the database, and if it fails, we try the fallback until there is no fallback
                if (Boolean.TRUE.equals(success)) {
                    logger.info("Successfully enabled database " + type);
                    chosenDatabases.put(clazz, type);
                    return CompletableFuture.completedFuture(true);
                }

                warning("Failed to enable database {0}, attempting fallback..", type);
                return tryEnableFallback(setup, fallback);
            });
    }

    private void warning(String message, Object... objects) {
        logger.log(Level.WARNING, message, objects);
    }

}
