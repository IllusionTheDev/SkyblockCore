package me.illusion.skyblockcore.common.platform;

import java.io.File;
import java.util.logging.Logger;

/**
 * The SkyblockPlatform interface is a template that includes all the common methods across all platforms, such as Spigot, Bungee, Velocity etc. This interface
 * is used to abstract away the platform specific code, and will be used when necessary in the Common module.
 */
public interface SkyblockPlatform {

    /**
     * Gets the logger for the platform
     *
     * @return The logger
     */
    Logger getLogger();

    /**
     * Gets the data folder for the platform
     *
     * @return The data folder
     */
    File getDataFolder();


}
