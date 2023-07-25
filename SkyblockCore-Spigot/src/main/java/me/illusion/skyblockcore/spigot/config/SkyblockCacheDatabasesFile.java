package me.illusion.skyblockcore.spigot.config;

import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabaseSetup;
import me.illusion.skyblockcore.spigot.utilities.config.BukkitConfigurationAdapter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockCacheDatabasesFile extends YMLBase implements SkyblockCacheDatabaseSetup {

    public SkyblockCacheDatabasesFile(JavaPlugin plugin) {
        super(plugin, "cache-database.yml");
    }

    @Override
    public ReadOnlyConfigurationSection getProperties(String databaseType) {
        FileConfiguration config = getConfiguration();
        ConfigurationSection section = config.getConfigurationSection(databaseType);

        if (section == null) {
            return null;
        }

        return BukkitConfigurationAdapter.adapt(section);
    }

    @Override
    public String getFallback(String databaseType) {
        FileConfiguration config = getConfiguration();
        ConfigurationSection databaseSection = config.getConfigurationSection(databaseType);

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
