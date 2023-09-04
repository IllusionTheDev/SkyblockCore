package me.illusion.skyblockcore.common.command.data;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandContext;

/**
 * Represents a command handler. This is the lambda that is called when a command is executed.
 *
 * @param <T> The type of audience this command handler will be used for. SkyblockAudience is the default.
 */
public interface SkyblockCommandHandler<T extends SkyblockAudience> {

    void handle(T audience, CommandContext context);

}
