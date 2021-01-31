package me.illusion.skyblockcore.spigot.pasting;

import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Location;

import java.io.File;

public interface PastingHandler {

    void paste(File[] file, Location loc);

    File[] save(Island island);

    PastingType getType();
}
