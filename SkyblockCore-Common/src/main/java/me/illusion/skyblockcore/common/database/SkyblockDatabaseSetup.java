package me.illusion.skyblockcore.common.database;

import java.util.Map;

/**
 * Represents a setup for a skyblock database. This class is used as a representation of the database.yml file, and is used to load the database.
 */
public interface SkyblockDatabaseSetup {

    /**
     * Gets the properties for a database type, this is used for loading the database
     *
     * @param databaseType The database type
     * @return The properties
     */
    // The reason why we return a map and not a config section is because this class works for all kinds of platforms, and is not limited to either bukkit or bungee
    Map<String, ?> getProperties(String databaseType);

    /**
     * Gets the fallback database type
     *
     * @param databaseType The database type
     * @return The fallback database type
     */
    String getFallback(String databaseType);

    /**
     * Gets the preferred database type
     *
     * @return The preferred database type
     */
    String getPreferredDatabase();

    /**
     * Gets whether the setuo supports file based databases
     *
     * @return TRUE if we should load file based databases, FALSE otherwise
     */
    boolean supportsFileBased();
}
