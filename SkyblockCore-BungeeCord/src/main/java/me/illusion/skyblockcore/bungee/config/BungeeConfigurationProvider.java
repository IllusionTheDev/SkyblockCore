package me.illusion.skyblockcore.bungee.config;

import java.io.File;
import java.io.IOException;
import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.bungee.utilities.config.BungeeConfigurationAdapter;
import me.illusion.skyblockcore.bungee.utilities.storage.BungeeYMLBase;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Bungee implementation of {@link ConfigurationProvider}.
 */
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
    public ConfigurationSection loadConfiguration(File file) {
        BungeeYMLBase base = new BungeeYMLBase(plugin, file, true);
        return BungeeConfigurationAdapter.adapt(file, this, "", base.getConfiguration());
    }

    @Override
    public void saveConfiguration(ConfigurationSection section, File file) {
        Configuration configuration = new Configuration();

        BungeeConfigurationAdapter.writeTo(section, configuration);

        net.md_5.bungee.config.ConfigurationProvider provider = net.md_5.bungee.config.ConfigurationProvider.getProvider(YamlConfiguration.class);

        try {
            provider.save(configuration, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
