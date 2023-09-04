package me.illusion.skyblockcore.common.command.manager;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.data.SkyblockCommandBuilder;

/**
 * Represents the proprietary command manager for SkyblockCore. This is used to register commands across all platforms.
 *
 * @param <T> The type of audience this command manager will be used for. SkyblockAudience is the default.
 */
public interface SkyblockCommandManager<T extends SkyblockAudience> {

    /**
     * Creates a new command builder.
     * @param name The name of the command. This is the first word after the slash.
     * @return The command builder.
     */
    SkyblockCommandBuilder<T> newCommand(String name);

    /**
     * Syncs all commands to the platform. Some platforms need this in order for commands to work after the server is running.
     */
    void syncCommands();


}
