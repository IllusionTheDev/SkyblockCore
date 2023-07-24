package me.illusion.skyblockcore.common.database;

import java.util.Map;

public interface SkyblockDatabaseSetup<DataType extends SkyblockDatabase> {

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
     * Checks whether the setup supports the database, there are some databases that are not supported by some structures
     *
     * @param database The database
     * @return TRUE if the setup supports the database, FALSE otherwise
     */
    boolean isSupported(DataType database);

    Class<DataType> getDatabaseClass();

}
