package me.illusion.skyblockcore.common.command.context.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.context.CommandContext;

/**
 * Represents a mutable command context. The mutable part is important when handling tab completion, as we slowly build up the arguments and can use the
 * arguments for further completion.
 */
public class MutatingCommandContext implements CommandContext {

    private final String fullInput;
    private final Map<String, Object> arguments = new LinkedHashMap<>();
    private String lastInput;

    public MutatingCommandContext(String fullInput) {
        this.fullInput = fullInput;
    }

    @Override
    public <T> T getArgument(String name) {
        return (T) arguments.get(name);
    }

    @Override
    public <T> T getArgument(int index) {
        return (T) arguments.values().toArray()[index];
    }

    @Override
    public String getFullInput() {
        return fullInput;
    }

    @Override
    public String getLastInput() {
        return lastInput;
    }

    public boolean addArgument(String input, CommandArgument argument) {
        lastInput = input;

        Object result = argument.parse(this);

        if (result == null) {
            return false;
        }

        arguments.put(argument.getName(), result);
        return true;
    }
}
