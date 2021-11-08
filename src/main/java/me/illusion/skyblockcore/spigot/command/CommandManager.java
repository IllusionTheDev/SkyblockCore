package me.illusion.skyblockcore.spigot.command;

import me.illusion.skyblockcore.spigot.command.comparison.ComparisonResultFull;

import java.util.List;

public interface CommandManager {

    void runTests();

    void register(SkyblockCommand command);

    ComparisonResultFull fullComparison(String identifier);

    SkyblockCommand get(String identifier);

    List<String> tabComplete(String identifier);


    SkyblockCommand get(String name, String... args);



}
