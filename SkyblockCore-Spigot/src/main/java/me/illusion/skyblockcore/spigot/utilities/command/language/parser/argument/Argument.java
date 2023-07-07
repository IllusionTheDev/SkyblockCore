package me.illusion.skyblockcore.spigot.utilities.command.language.parser.argument;

import me.illusion.skyblockcore.spigot.utilities.command.language.parser.ArgumentType;

public class Argument<T> {

    private final String name;
    private final ArgumentType type;
    private boolean optional;

    private Object value;

    public Argument(String name, ArgumentType type, boolean optional, Object value) {
        this.name = name;
        this.type = type;
        this.optional = optional;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public ArgumentType getArgumentType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Argument<T> clone() {
        return new Argument<>(name, type, optional, value);
    }

    @Override
    public String toString() {
        return "Argument{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", optional=" + optional +
            ", value=" + value +
            '}';
    }
}
