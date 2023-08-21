package me.illusion.skyblockcore.common.command.data;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandContext;

public interface SkyblockCommandHandler<T extends SkyblockAudience> {

    void handle(T audience, CommandContext context);

}
