package me.illusion.skyblockcore.spigot.utilities.command.language.type;

public class Parameter<Type> {

    private final String name;
    private final ParameterType<Type> type;

    private final boolean optional;

    public Parameter(String name, ParameterType<Type> type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    public String getName() {
        return name;
    }

    public ParameterType<Type> getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public Type parse(String input) {
        return type.parse(input);
    }

}
