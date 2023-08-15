package me.illusion.skyblockcore.common.config;

import java.io.File;

public interface ConfigurationProvider {

    File getDataFolder();

    ReadOnlyConfigurationSection loadConfiguration(File file);

}
