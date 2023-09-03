package me.illusion.skyblockcore.common.config.impl;

import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * Represents the database configuration file.
 */
public class SkyblockDatabasesFile extends AbstractDatabaseConfiguration<SkyblockFetchingDatabase> {

    public SkyblockDatabasesFile(SkyblockPlatform platform) {
        super(platform, "database.yml");
    }


    @Override
    public Class<SkyblockFetchingDatabase> getDatabaseClass() {
        return SkyblockFetchingDatabase.class;
    }
}
