package me.illusion.skyblockcore.common.config;

import java.io.File;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * Represents an abstract configuration, which is used to load and store configuration files.
 */
public abstract class AbstractConfiguration {

    private final File file;
    private final ConfigurationProvider provider;
    protected ConfigurationSection configuration;

    protected AbstractConfiguration(SkyblockPlatform platform, String fileName) {
        provider = platform.getConfigurationProvider();

        this.file = new File(provider.getDataFolder(), fileName);
        reload();
    }

    /**
     * Obtains the configuration section of this configuration.
     *
     * @return The configuration section of this configuration.
     */
    public ConfigurationSection getConfiguration() {
        return configuration;
    }

    /**
     * Obtains the file that this configuration is read from.
     *
     * @return The file that this configuration is read from.
     */
    public File getFile() {
        return file;
    }

    /**
     * Reloads the configuration.
     */
    public void reload() {
        configuration = provider.loadConfiguration(file);
    }
}
