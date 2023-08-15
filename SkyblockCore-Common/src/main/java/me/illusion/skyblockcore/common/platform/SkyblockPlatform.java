package me.illusion.skyblockcore.common.platform;

import java.io.File;
import java.util.logging.Logger;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;

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

    /**
     * Gets the database registry for the platform
     *
     * @return The database registry
     */
    SkyblockDatabaseRegistry getDatabaseRegistry();

    /**
     * Gets the event manager for the platform
     *
     * @return The event manager
     */
    SkyblockEventManager getEventManager();

    /**
     * Gets the configuration provider for the platform, used to load configuration files
     *
     * @return The configuration provider
     */
    ConfigurationProvider getConfigurationProvider();


}
