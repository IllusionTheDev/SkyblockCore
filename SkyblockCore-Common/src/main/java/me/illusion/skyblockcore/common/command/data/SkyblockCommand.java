package me.illusion.skyblockcore.common.command.data;

import java.util.List;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;

/**
 * Represents an instance of a Skyblock command.
 *
 * @param <T> The type of audience this command will be used for. SkyblockAudience is the default.
 */
public interface SkyblockCommand<T extends SkyblockAudience> {

    /**
     * Gets all the arguments for this command.
     * @return The arguments.
     */
    List<CommandArgument> getArguments();

    /**
     * Gets the handler for this command.
     * @return The handler.
     */
    SkyblockCommandHandler<T> getHandler();

    /**
     * Gets the target audience for this command.
     * @return The target audience.
     */
    Class<T> getAudience();

    /**
     * Gets the permission for this command.
     * @return The permission.
     */
    String getPermission();

}
