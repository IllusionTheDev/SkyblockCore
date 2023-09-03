package me.illusion.skyblockcore.common.config.impl;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseSetup;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public abstract class AbstractDatabaseConfiguration<T extends SkyblockDatabase> extends AbstractConfiguration implements SkyblockDatabaseSetup<T> {

    private boolean supportsFileBased = true;

    protected AbstractDatabaseConfiguration(SkyblockPlatform platform, String fileName) {
        super(platform, fileName);
    }

    public ReadOnlyConfigurationSection getProperties(String databaseType) {
        return getConfiguration().getSection(databaseType);
    }

    public String getFallback(String databaseType) {
        ReadOnlyConfigurationSection databaseSection = getProperties(databaseType);

        if (databaseSection == null) {
            return null;
        }

        return databaseSection.getString("fallback");
    }

    public String getPreferredDatabase() {
        return getConfiguration().getString("preferred");
    }

    public boolean supportsFileBased() {
        return supportsFileBased;
    }

    public void setSupportsFileBased(boolean supportsFileBased) { // This can be set by the network type
        this.supportsFileBased = supportsFileBased;
    }

    public boolean isSupported(T database) {
        return !database.isFileBased() || supportsFileBased(); // If the database is file based, we must also support file based databases
    }

}
