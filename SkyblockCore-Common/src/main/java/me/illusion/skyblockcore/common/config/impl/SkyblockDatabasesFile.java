package me.illusion.skyblockcore.common.config.impl;

import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabaseSetup;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * Represents the database configuration file.
 */
public class SkyblockDatabasesFile extends AbstractDatabaseConfiguration implements SkyblockFetchingDatabaseSetup {

    private boolean supportsFileBased = true;

    public SkyblockDatabasesFile(SkyblockPlatform platform) {
        super(platform, "database.yml");
    }

    @Override
    public boolean isSupported(SkyblockFetchingDatabase database) {
        return !database.isFileBased() || supportsFileBased(); // If the database is file based, we must also support file based databases
    }

    @Override
    public boolean supportsFileBased() {
        return supportsFileBased;
    }

    public void setSupportsFileBased(boolean supportsFileBased) { // This can be set by the network type
        this.supportsFileBased = supportsFileBased;
    }
}
