package me.illusion.skyblockcore.common.config;

import java.io.File;

/**
 * Represents a configuration provider, which is used to load configuration files.
 */
public interface ConfigurationProvider {

    /**
     * Obtains the data folder of the platform.
     *
     * @return The data folder of the platform.
     */
    File getDataFolder();

    /**
     * Loads a configuration from a file.
     *
     * @param file The file to load the configuration from.
     * @return The configuration section of the file.
     */
    ReadOnlyConfigurationSection loadConfiguration(File file);

    /**
     * Loads a configuration from a file name.
     *
     * @param fileName The name of the file to load the configuration from.
     * @return The configuration section of the file.
     */
    default ReadOnlyConfigurationSection loadConfiguration(String fileName) {
        return loadConfiguration(new File(getDataFolder(), fileName));
    }

}
