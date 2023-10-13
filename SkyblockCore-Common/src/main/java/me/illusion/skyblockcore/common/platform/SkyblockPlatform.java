package me.illusion.skyblockcore.common.platform;

import java.util.logging.Logger;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.databaserewrite.registry.SkyblockDatabaseRegistry;
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

    /**
     * Gets the command manager for the platform
     *
     * @return The command manager
     */
    SkyblockCommandManager<SkyblockAudience> getCommandManager();

    /**
     * Gets the messages file for the platform. Each platform may have its own messages file, and access to this is required.
     *
     * @return The messages file
     */
    SkyblockMessagesFile getMessagesFile();

    /**
     * Disables the platform due to an initialization error (e.g. incorrect configuration)
     */
    void disableExceptionally();


}
