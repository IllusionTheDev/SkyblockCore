package me.illusion.skyblockcore.spigot.pasting;

import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Location;

import java.io.File;
import java.util.function.Consumer;

public interface PastingHandler {

    void paste(File[] file, Location loc);

    void save(Island island, Consumer<File[]> action);

    PastingType getType();
}
