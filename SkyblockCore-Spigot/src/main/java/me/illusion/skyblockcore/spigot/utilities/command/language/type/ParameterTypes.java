package me.illusion.skyblockcore.spigot.utilities.command.language.type;

import me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.IntegerParameterType;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.NumericalParameterType;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.StringParameterType;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.filtered.TimeParameterType;
import me.illusion.skyblockcore.spigot.utilities.command.language.unit.MinecraftTime;

public class ParameterTypes {

    public static final ParameterType<String> STRING = new StringParameterType();
    public static final ParameterType<Integer> INTEGER = new IntegerParameterType();
    public static final ParameterType<Double> NUMERICAL = new NumericalParameterType();
    public static final ParameterType<MinecraftTime> MINECRAFT_TIME = new TimeParameterType();

}
