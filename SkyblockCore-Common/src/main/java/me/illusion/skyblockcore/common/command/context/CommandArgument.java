package me.illusion.skyblockcore.common.command.context;

import java.util.Collections;
import java.util.List;
import me.illusion.skyblockcore.common.command.context.arg.ProxiedDefaultArgument;

/**
 * Represents a command argument. This will then be parsed by the command context.
 */
public interface CommandArgument {

    /**
     * Gets the name of this argument. This is used to retrieve the argument from the command context.
     *
     * @return The name of this argument.
     */
    String getName();

    /**
     * Parses the argument from the command context into an object.
     * @param context The command context.
     * @return The parsed object.
     */
    Object parse(CommandContext context);

    /**
     * Gets the tab completion for this argument.
     * @param context The command context.
     * @return The tab completion.
     */
    default List<String> tabComplete(CommandContext context) {
        return Collections.emptyList();
    }

    default CommandArgument orDefault(Object defaultValue) {
        return new ProxiedDefaultArgument(this, defaultValue);
    }

}
