package me.illusion.skyblockcore.common.config;

import java.io.File;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public abstract class AbstractConfiguration {

    private final File file;
    private final ConfigurationProvider provider;
    protected ReadOnlyConfigurationSection configuration;

    protected AbstractConfiguration(SkyblockPlatform platform, String fileName) {
        provider = platform.getConfigurationProvider();

        this.file = new File(provider.getDataFolder(), fileName);
        reload();
    }

    public ReadOnlyConfigurationSection getConfiguration() {
        return configuration;
    }

    public File getFile() {
        return file;
    }

    public void reload() {
        configuration = provider.loadConfiguration(file);
    }
}
