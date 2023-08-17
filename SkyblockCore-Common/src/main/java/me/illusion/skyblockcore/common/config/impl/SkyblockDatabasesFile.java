package me.illusion.skyblockcore.common.config.impl;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabaseSetup;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * Represents the database configuration file.
 */
public class SkyblockDatabasesFile extends AbstractConfiguration implements SkyblockFetchingDatabaseSetup {

    private boolean supportsFileBased = true;

    public SkyblockDatabasesFile(SkyblockPlatform platform) {
        super(platform, "database.yml");
    }

    @Override
    public ReadOnlyConfigurationSection getProperties(String databaseType) {
        return getConfiguration().getSection(databaseType);
    }

    @Override
    public String getFallback(String databaseType) {
        ReadOnlyConfigurationSection databaseSection = getProperties(databaseType);

        if (databaseSection == null) {
            return null;
        }

        return databaseSection.getString("fallback");
    }

    @Override
    public String getPreferredDatabase() {
        return getConfiguration().getString("preferred");
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
