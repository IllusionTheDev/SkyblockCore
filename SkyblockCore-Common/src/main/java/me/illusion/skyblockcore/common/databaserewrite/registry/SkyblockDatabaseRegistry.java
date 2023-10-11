package me.illusion.skyblockcore.common.databaserewrite.registry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public class SkyblockDatabaseRegistry {

    private final Map<String, RegisteredDatabase<?>> registeredDatabases = new ConcurrentHashMap<>();
    private final SkyblockDatabaseCredentialRegistry credentialRegistry = new SkyblockDatabaseCredentialRegistry();

    private final SkyblockPlatform platform;

    public SkyblockDatabaseRegistry(SkyblockPlatform platform) {
        this.platform = platform;
    }

    public <T extends SkyblockDatabase> void register(String name, SkyblockDatabaseProvider<T> provider) {
        registeredDatabases.put(name, new RegisteredDatabase<>(provider, name));
    }

    public CompletableFuture<Void> loadPossible(ReadOnlyConfigurationSection section) {
        loadSection(section);
        credentialRegistry.checkCyclicDependencies();

        Set<CompletableFuture<?>> futures = new HashSet<>();

        for (RegisteredDatabase<?> registeredDatabase : registeredDatabases.values()) {
            if (registeredDatabase.isEnabled()) {
                continue;
            }

            ReadOnlyConfigurationSection credentials = credentialRegistry.getCredentials(registeredDatabase.getName());

            if (credentials == null) {
                continue;
            }

            String type = credentials.getString("type");

            if (type == null) {
                warn("No type specified for database credentials of name {0}", registeredDatabase.getName());
                continue;
            }

            registeredDatabase.setSpecifiedType(type);
            SkyblockDatabase database = registeredDatabase.getDatabase();

            if (database == null) {
                warn("No database found for name {0}", registeredDatabase.getName());
                continue;
            }

            registeredDatabase.setEnabled(true);
            futures.add(database.enable(credentials));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void loadSection(ReadOnlyConfigurationSection section) {
        for (String key : section.getKeys()) {
            Object obj = section.get(key);

            if (obj instanceof ReadOnlyConfigurationSection) {
                credentialRegistry.registerCredentials(key, (ReadOnlyConfigurationSection) obj);
                continue;
            }

            String value = String.valueOf(obj);
            credentialRegistry.registerDependency(key, value);
        }
    }

    public boolean areAllLoaded() {
        for (RegisteredDatabase<?> registeredDatabase : registeredDatabases.values()) {
            if (!registeredDatabase.isEnabled()) {
                return false;
            }
        }

        return true;
    }

    private void warn(String message, Object... args) {
        platform.getLogger().log(Level.WARNING, message, args);
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