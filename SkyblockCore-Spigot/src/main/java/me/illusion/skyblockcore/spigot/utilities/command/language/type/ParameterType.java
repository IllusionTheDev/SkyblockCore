package me.illusion.skyblockcore.spigot.utilities.command.language.type;

public interface ParameterType<Type> {

    boolean isType(String input);

    Type parse(String input);

    Type getDefaultValue();

}
