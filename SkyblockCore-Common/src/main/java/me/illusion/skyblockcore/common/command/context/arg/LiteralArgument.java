package me.illusion.skyblockcore.common.command.context.arg;

import java.util.Collections;
import java.util.List;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.context.CommandContext;

/**
 * Represents a "literal" argument, which matches the input exactly.
 */
public class LiteralArgument implements CommandArgument {

    private final String name;

    public LiteralArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object parse(CommandContext context) {
        if (context.getLastInput().equalsIgnoreCase(name)) {
            return name;
        }

        return null;
    }

    @Override
    public List<String> tabComplete(CommandContext context) {
        return Collections.singletonList(name);
    }
}
