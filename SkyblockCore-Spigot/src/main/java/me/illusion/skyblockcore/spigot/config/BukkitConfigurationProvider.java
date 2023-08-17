package me.illusion.skyblockcore.spigot.config;

import java.io.File;
import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.utilities.config.BukkitConfigurationAdapter;

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
    public ReadOnlyConfigurationSection loadConfiguration(File file) {
        YMLBase base = new YMLBase(plugin, file, true);
        return BukkitConfigurationAdapter.adapt(base.getConfiguration());
    }
}
