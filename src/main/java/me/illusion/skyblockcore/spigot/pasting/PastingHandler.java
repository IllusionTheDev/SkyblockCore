package me.illusion.skyblockcore.spigot.pasting;

import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface PastingHandler {

    default CompletableFuture<Void> paste(SerializedFile[] file, Location loc) {
        return null; // Used when requiresLoadedWorld() is true
    }

    default CompletableFuture<Void> paste(SerializedFile[] file, String worldName, Vector point) {
        return null; // Used when requiresLoadedWorld() is false
    }

    void save(Island island, Consumer<SerializedFile[]> action);

    PastingType getType();

    boolean requiresLoadedWorld();
}
