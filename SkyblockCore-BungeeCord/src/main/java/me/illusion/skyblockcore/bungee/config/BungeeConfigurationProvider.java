package me.illusion.skyblockcore.bungee.config;

import java.io.File;
import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.bungee.utilities.config.BungeeConfigurationAdapter;
import me.illusion.skyblockcore.bungee.utilities.storage.BungeeYMLBase;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;

public class BungeeConfigurationProvider implements ConfigurationProvider {

    private final SkyblockBungeePlugin plugin;

    public BungeeConfigurationProvider(SkyblockBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public ReadOnlyConfigurationSection loadConfiguration(File file) {
        BungeeYMLBase base = new BungeeYMLBase(plugin, file, true);
        return BungeeConfigurationAdapter.adapt("", base.getConfiguration());
    }
}
