package me.illusion.skyblockcore.common.database;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import me.illusion.skyblockcore.common.database.fetching.mongo.MongoSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.MariaDBSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.MySQLSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.PostgresSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.impl.SQLiteSkyblockDatabase;
import me.illusion.skyblockcore.common.database.structure.SkyblockDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public class SkyblockDatabaseRegistry {

    private final Map<String, SkyblockDatabase> databases = new ConcurrentHashMap<>();
    private final Logger logger;
    private String chosenDatabase;

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
    public SkyblockDatabase getChosenDatabase() {
        return databases.get(chosenDatabase);
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
    }

    /**
     * Tries to enable the preferred database, and if it fails, tries to enable the fallback
     *
     * @param setup The setup to use
     * @return A completable future that completes when the database is enabled
     */
    public CompletableFuture<Boolean> tryEnable(SkyblockDatabaseSetup setup) {
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
     * @return A completable future that completes when the database is enabled
     */
    private CompletableFuture<Boolean> tryEnableFallback(SkyblockDatabaseSetup setup, String type) {
        if (type == null) { // If the fallback is null, we can't do anything, so we just return false
            return CompletableFuture.completedFuture(false);
        }

        SkyblockDatabase database = databases.get(type);
        String fallback = setup.getFallback(type);

        if (database == null) { // If the database specified doesn't exist, we try the fallback
            logger.warning("Failed to find database " + type + ", attempting fallback..");
            return tryEnableFallback(setup, fallback);
        }

        if (database.isFileBased() && !setup.supportsFileBased()) {
            logger.warning(type + " is file based, and not supported in this current setup, attempting fallback..");
            return tryEnableFallback(setup, fallback); // If the database is file based, and the setup doesn't support file based databases, we try the fallback
        }

        Map<String, ?> properties = setup.getProperties(type);

        if (properties == null) { // If there are no properties to load from, we try the fallback
            logger.warning("Failed to find properties for " + type + ", attempting fallback..");
            return tryEnableFallback(setup, fallback);
        }

        return database.enable(properties)
            .thenCompose(success -> { // We try to enable the database, and if it fails, we try the fallback until there is no fallback
                if (success) {
                    logger.info("Successfully enabled database " + type);
                    chosenDatabase = type;
                    return CompletableFuture.completedFuture(true);
                }

                logger.warning("Failed to enable database " + type + ", attempting fallback..");
                return tryEnableFallback(setup, fallback);
            });
    }

}