package me.illusion.skyblockcore.bungee.config;

import me.illusion.skyblockcore.bungee.utilities.config.BungeeConfigurationAdapter;
import me.illusion.skyblockcore.bungee.utilities.storage.BungeeYMLBase;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabaseSetup;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;


public class SkyblockCacheDatabasesFile extends BungeeYMLBase implements SkyblockCacheDatabaseSetup {

    public SkyblockCacheDatabasesFile(Plugin plugin) {
        super(plugin, "cache-database.yml");
    }

    @Override
    public ReadOnlyConfigurationSection getProperties(String databaseType) {
        Configuration config = getConfiguration();
        Configuration section = config.getSection(databaseType);

        if (section == null) {
            return null;
        }

        return BungeeConfigurationAdapter.adapt(databaseType, section);
    }

    @Override
    public String getFallback(String databaseType) {
        Configuration config = getConfiguration();
        Configuration databaseSection = config.getSection(databaseType);

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

    @Override
    public Class<SkyblockCacheDatabase> getDatabaseClass() {
        return SkyblockCacheDatabase.class;
    }
}
