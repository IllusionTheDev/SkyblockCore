package me.illusion.skyblockcore.common.database.fetching;

import me.illusion.skyblockcore.common.database.SkyblockDatabaseSetup;

/**
 * Represents a setup for a skyblock database. This class is used as a representation of the database.yml file, and is used to load the database.
 */
public interface SkyblockFetchingDatabaseSetup extends SkyblockDatabaseSetup<SkyblockFetchingDatabase> {

    /**
     * Gets whether the setuo supports file based databases
     *
     * @return TRUE if we should load file based databases, FALSE otherwise
     */
    boolean supportsFileBased();

    @Override
    default Class<SkyblockFetchingDatabase> getDatabaseClass() {
        return SkyblockFetchingDatabase.class;
    }
}
