package me.illusion.skyblockcore.spigot.pasting;

import me.illusion.skyblockcore.spigot.data.SerializedFile;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Location;

import java.util.function.Consumer;

public interface PastingHandler {

    void paste(SerializedFile[] file, Location loc);

    void save(Island island, Consumer<SerializedFile[]> action);

    PastingType getType();
}
