package me.illusion.skyblockcore.common.database.cache;

import me.illusion.skyblockcore.common.database.SkyblockDatabaseSetup;

/**
 * The setup for the cache database, this is used for loading the cache database. Currently, the cache setup just mimics the database setup, but this may change
 * in the future.
 */
public interface SkyblockCacheDatabaseSetup extends SkyblockDatabaseSetup<SkyblockCacheDatabase> {

    default Class<SkyblockCacheDatabase> getDatabaseClass() {
        return SkyblockCacheDatabase.class;
    }
}
