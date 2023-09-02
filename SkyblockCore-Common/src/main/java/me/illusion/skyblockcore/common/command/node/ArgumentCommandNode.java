package me.illusion.skyblockcore.common.command.node;

import lombok.Getter;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.data.SkyblockCommand;

public class ArgumentCommandNode extends AbstractCommandNode {

    @Getter
    private final SkyblockCommand<?> command;
    private final CommandArgument argument;

    public ArgumentCommandNode(SkyblockCommand<?> command, CommandArgument argument) {
        this.command = command;
        this.argument = argument;
    }

    @Override
    public String getName() {
        return argument.getName();
    }

    @Override
    public String getPermission() {
        return command.getPermission();
    }

    @Override
    public Class<? extends SkyblockAudience> getTargetAudience() {
        return command.getAudience();
    }

}
