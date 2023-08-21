package me.illusion.skyblockcore.common.command.data;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;

public interface SkyblockCommandBuilder<T extends SkyblockAudience> {

    SkyblockCommandBuilder<T> registerArgument(CommandArgument argument);

    SkyblockCommandBuilder<T> handler(SkyblockCommandHandler<T> handler);

    <V extends SkyblockAudience> SkyblockCommandBuilder<V> audience(Class<V> audience);

    SkyblockCommand<T> build();

}
