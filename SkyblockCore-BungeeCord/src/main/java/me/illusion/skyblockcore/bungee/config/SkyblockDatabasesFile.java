package me.illusion.skyblockcore.bungee.config;

import me.illusion.skyblockcore.bungee.utilities.config.BungeeConfigurationAdapter;
import me.illusion.skyblockcore.bungee.utilities.storage.BungeeYMLBase;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabaseSetup;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class SkyblockDatabasesFile extends BungeeYMLBase implements SkyblockFetchingDatabaseSetup {

    public SkyblockDatabasesFile(Plugin plugin) {
        super(plugin, "database.yml");
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
    public boolean isSupported(SkyblockFetchingDatabase database) {
        return !database.isFileBased() || supportsFileBased(); // If the database is file based, we must also support file based databases
    }

    @Override
    public Class<SkyblockFetchingDatabase> getDatabaseClass() {
        return SkyblockFetchingDatabase.class;
    }

    @Override
    public boolean supportsFileBased() {
        return false;
    }
}
