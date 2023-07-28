package me.illusion.skyblockcore.common.platform;

import java.io.File;
import java.util.logging.Logger;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.common.profile.SkyblockProfileCache;

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
     * Gets the profile cache for the platform
     *
     * @return The profile cache
     */
    SkyblockProfileCache getProfileCache();

    /**
     * Gets the event manager for the platform
     *
     * @return The event manager
     */
    SkyblockEventManager getEventManager();


}
