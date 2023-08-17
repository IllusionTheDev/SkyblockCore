package me.illusion.skyblockcore.common.config.impl;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public class AbstractDatabaseConfiguration extends AbstractConfiguration {

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
}
