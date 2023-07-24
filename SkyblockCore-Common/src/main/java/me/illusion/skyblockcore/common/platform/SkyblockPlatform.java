package me.illusion.skyblockcore.common.platform;

import java.io.File;
import java.util.logging.Logger;

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
