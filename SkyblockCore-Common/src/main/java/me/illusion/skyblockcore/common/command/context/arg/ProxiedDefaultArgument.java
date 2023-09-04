package me.illusion.skyblockcore.common.command.context.arg;

import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.context.CommandContext;

/**
 * Represents a command argument with a default value.
 */
public class ProxiedDefaultArgument implements CommandArgument {

    private final CommandArgument argument;
    private final Object value;

    public ProxiedDefaultArgument(CommandArgument argument, Object value) {
        this.argument = argument;
        this.value = value;
    }

    @Override
    public String getName() {
        return argument.getName();
    }

    @Override
    public Object parse(CommandContext context) {
        Object result = argument.parse(context);

        if (result == null) {
            return value;
        }

        return result;
    }

}
