package me.illusion.skyblockcore.common.config.impl;

import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * Represents the cache database configuration file.
 */
public class SkyblockCacheDatabasesFile extends AbstractDatabaseConfiguration<SkyblockCacheDatabase> {

    public SkyblockCacheDatabasesFile(SkyblockPlatform platform) {
        super(platform, "cache-database.yml");
    }

    @Override
    public Class<SkyblockCacheDatabase> getDatabaseClass() {
        return SkyblockCacheDatabase.class;
    }
}
