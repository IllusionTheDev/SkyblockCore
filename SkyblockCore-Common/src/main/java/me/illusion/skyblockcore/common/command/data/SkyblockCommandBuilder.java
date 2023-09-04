package me.illusion.skyblockcore.common.command.data;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;

/**
 * Represents a command builder. This is used to build commands.
 *
 * @param <T> The type of audience this command will be used for. SkyblockAudience is the default.
 */
public interface SkyblockCommandBuilder<T extends SkyblockAudience> {

    /**
     * Registers an argument to the command.
     * @param argument The argument to register.
     * @return The command builder.
     */
    SkyblockCommandBuilder<T> registerArgument(CommandArgument argument);

    /**
     * Sets the handler for the command.
     * @param handler The handler to set.
     * @return The command builder.
     */
    SkyblockCommandBuilder<T> handler(SkyblockCommandHandler<T> handler);

    /**
     * Sets the permission for the command.
     * @param permission The permission to set.
     * @return The command builder.
     */
    SkyblockCommandBuilder<T> permission(String permission);

    /**
     * Sets the new target audience for the command.
     * @param audience The audience to set.
     * @return A new command builder of the new audience type.
     * @param <V> The type of audience this command will be used for.
     */
    <V extends SkyblockAudience> SkyblockCommandBuilder<V> audience(Class<V> audience);

    /**
     * Builds and registers the command.
     * @return The built command.
     */
    SkyblockCommand<T> build();

}
