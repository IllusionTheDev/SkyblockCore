package me.illusion.skyblockcore.common.command.manager;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.data.SkyblockCommandBuilder;

public interface SkyblockCommandManager<T extends SkyblockAudience> {

    SkyblockCommandBuilder<T> newCommand(String name);

    void syncCommands();


}
