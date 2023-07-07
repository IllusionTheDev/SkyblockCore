package me.illusion.skyblockcore.spigot.utilities.command.language.type.impl;

import me.illusion.skyblockcore.spigot.utilities.command.language.type.ParameterType;


public class IntegerParameterType implements ParameterType<Integer> {

    @Override
    public boolean isType(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Integer parse(String input) {
        return Integer.parseInt(input);
    }

    @Override
    public Integer getDefaultValue() {
        return 1;
    }
}
