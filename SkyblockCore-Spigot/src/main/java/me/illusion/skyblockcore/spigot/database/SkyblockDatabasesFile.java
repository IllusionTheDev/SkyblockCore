package me.illusion.skyblockcore.spigot.database;

import java.util.Map;
import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseSetup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockDatabasesFile extends YMLBase implements SkyblockDatabaseSetup {

    private boolean supportsFileBased = true;

    public SkyblockDatabasesFile(JavaPlugin plugin) {
        super(plugin, "database.yml");
    }

    @Override
    public Map<String, ?> getProperties(String databaseType) {
        FileConfiguration config = getConfiguration();
        ConfigurationSection section = config.getConfigurationSection(databaseType);

        if (section == null) {
            return null;
        }

        return asMap(section);
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
    public boolean supportsFileBased() {
        return supportsFileBased;
    }

    public void setSupportsFileBased(boolean supportsFileBased) { // This can be set by the network type
        this.supportsFileBased = supportsFileBased;
    }

    private Map<String, ?> asMap(ConfigurationSection section) {
        return section.getValues(false);
    }
}
