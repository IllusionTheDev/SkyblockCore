package me.illusion.skyblockcore.common.config;

import java.io.File;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;

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
    ConfigurationSection loadConfiguration(File file);

    /**
     * Loads a configuration from a file name.
     *
     * @param fileName The name of the file to load the configuration from.
     * @return The configuration section of the file.
     */
    default ConfigurationSection loadConfiguration(String fileName) {
        return loadConfiguration(new File(getDataFolder(), fileName));
    }

    /**
     * Saves a configuration to a file.
     *
     * @param section The configuration section to save.
     * @param file    The file to save the configuration to.
     */
    void saveConfiguration(ConfigurationSection section, File file);

}
