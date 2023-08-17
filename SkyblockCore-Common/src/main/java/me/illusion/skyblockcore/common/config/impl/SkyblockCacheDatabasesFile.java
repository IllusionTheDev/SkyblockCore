package me.illusion.skyblockcore.common.config.impl;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabaseSetup;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public class SkyblockCacheDatabasesFile extends AbstractConfiguration implements SkyblockCacheDatabaseSetup {

    public SkyblockCacheDatabasesFile(SkyblockPlatform platform) {
        super(platform, "cache-database.yml");
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
    public boolean isSupported(SkyblockCacheDatabase database) {
        return true;
    }

}
