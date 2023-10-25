package me.illusion.skyblockcore.spigot.config;

import java.io.File;
import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.utilities.config.BukkitConfigurationAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Bukkit implementation of {@link ConfigurationProvider}.
 */
public class BukkitConfigurationProvider implements ConfigurationProvider {

    private final SkyblockSpigotPlugin plugin;

    public BukkitConfigurationProvider(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public ConfigurationSection loadConfiguration(File file) {
        YMLBase base = new YMLBase(plugin, file, true);
        return BukkitConfigurationAdapter.adapt(file, this, base.getConfiguration());
    }

    @Override
    public void saveConfiguration(ConfigurationSection section, File file) {
        FileConfiguration configuration = new YamlConfiguration();
        BukkitConfigurationAdapter.writeTo(section, configuration);

        try {
            configuration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
