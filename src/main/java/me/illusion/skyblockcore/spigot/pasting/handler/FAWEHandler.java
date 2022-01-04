package me.illusion.skyblockcore.spigot.pasting.handler;

import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import me.illusion.skyblockcore.spigot.pasting.provider.FAWEProvider;
import me.illusion.skyblockcore.spigot.pasting.provider.NewFAWEProvider;
import me.illusion.skyblockcore.spigot.pasting.provider.OldFAWEProvider;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static me.illusion.skyblockcore.spigot.pasting.PastingType.FAWE;

public class FAWEHandler implements PastingHandler {

    private final SkyblockPlugin main;

    private FAWEProvider provider;

    public FAWEHandler(SkyblockPlugin main) {
        this.main = main;

        try {
            Class.forName("com.sk89q.worldedit.math.BlockVector3");
            provider = new NewFAWEProvider();
        } catch (ClassNotFoundException e) {
            provider = new OldFAWEProvider();
        }
    }

    @SneakyThrows
    private void paste(SerializedFile serializedFile, Location loc) {
        serializedFile
                .getFile()
                .thenAccept(file -> provider.paste(file, loc));
    }


    @Override
    public CompletableFuture<Void> paste(SerializedFile[] file, Location loc) {
        for (SerializedFile f : file)
            paste(f, loc);

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void save(Island island, Consumer<SerializedFile[]> action) {
        File file = new File(main.getDataFolder() + File.separator + "cache", island.getData().getId() + ".schematic");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Location p1 = island.getPointOne();
        Location p2 = island.getPointTwo();

        provider.save(file, p1, p2);
        action.accept(array(new SerializedFile(file)));
    }

    @SafeVarargs
    private final <T> T[] array(T... types) {
        return types;
    }

    @Override
    public PastingType getType() {
        return FAWE;
    }

    @Override
    public boolean requiresLoadedWorld() {
        return true;
    }
}
