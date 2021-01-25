package me.illusion.skyblockcore.pasting;

import me.illusion.skyblockcore.island.Island;
import org.bukkit.Location;

import java.io.File;

public interface PastingHandler {

    void paste(File[] file, Location loc);

    File[] save(Island island);

    PastingType getType();
}
