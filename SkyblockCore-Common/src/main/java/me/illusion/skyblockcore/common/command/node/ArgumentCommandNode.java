package me.illusion.skyblockcore.common.command.node;

import lombok.Getter;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.data.SkyblockCommand;

/**
 * Represents a command node version of a CommandArgument.
 */
@Getter
public class ArgumentCommandNode extends AbstractCommandNode {

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
