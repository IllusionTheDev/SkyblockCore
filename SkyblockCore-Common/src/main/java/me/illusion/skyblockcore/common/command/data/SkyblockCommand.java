package me.illusion.skyblockcore.common.command.data;

import java.util.List;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;

public interface SkyblockCommand<T extends SkyblockAudience> {

    List<CommandArgument> getArguments();

    SkyblockCommandHandler<T> getHandler();

    void register();

}
