package me.illusion.skyblockcore.spigot.pasting;

import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Location;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface PastingHandler {

    CompletableFuture<Void> paste(SerializedFile[] file, Location loc);

    void save(Island island, Consumer<SerializedFile[]> action);

    PastingType getType();
}
