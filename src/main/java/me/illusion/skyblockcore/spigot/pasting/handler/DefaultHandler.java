package me.illusion.skyblockcore.spigot.pasting.handler;

import com.google.common.io.Files;
import me.illusion.skyblockcore.shared.environment.Core;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import me.illusion.skyblockcore.spigot.utilities.WorldUtils;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static me.illusion.skyblockcore.shared.utilities.CollectionUtils.allOf;

public class DefaultHandler implements PastingHandler {

    private final SkyblockPlugin main;
    private String extension;

    public DefaultHandler(SkyblockPlugin main) {
        this.main = main;
    }

    private CompletableFuture<Void> paste(SerializedFile serializedFile, String name) {
        return serializedFile.getFile().thenAccept((file) -> {
            if (extension == null)
                extension = getExtension(file.getName());

            // Obtain the region folder for the world
            File regionFolder = new File(Bukkit.getWorldContainer() + File.separator + name + File.separator + "region");

            writeFile(regionFolder, file);
        });
        // Re-load world
    }

    private void writeFile(File regionFolder, File finalFile) {
        // Create the new file
        File newFile = new File(regionFolder, finalFile.getName());

        try {
            // Create the region file
            newFile.createNewFile();
            // Copy the file
            Files.copy(finalFile, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> paste(SerializedFile[] file, String name, Vector point) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        CountDownLatch mainLatch = new CountDownLatch(file.length);

        File regionFolder = new File(Bukkit.getWorldContainer() + File.separator + name + File.separator + "region");

        regionFolder.delete();
        regionFolder.mkdir();

        for (SerializedFile f : file)
            futures.add(paste(f, name));

        allOf(futures).thenRun(() -> {
            Core.info("Done pasting");
            mainLatch.countDown();
        });

        return CompletableFuture.runAsync(() -> wait(mainLatch));


    }

    @Override
    public void save(Island island, Consumer<SerializedFile[]> action) {
        WorldUtils.save(main, island.getWorld(), (world) -> {
            File regionFolder = new File(Bukkit.getWorldContainer() + File.separator + island.getWorld() + File.separator + "region");

            Location one = island.getPointOne();
            Location two = island.getPointTwo();

            new ScheduleBuilder(main)
                    .in(main.getSettings().getSaveDelay()).ticks()
                    .run(() -> {
                        File[] worldFiles = WorldUtils.getAllFilesBetween(regionFolder, one, two);
                        SerializedFile[] files = SerializedFile.loadArray(worldFiles);

                        action.accept(files);
                    })
                    .sync()
                    .start();

        });
    }

    @Override
    public PastingType getType() {
        return PastingType.DEFAULT;
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return filename.substring(index + 1);
    }

    private void wait(CountDownLatch latch) {
        try {
            WorldUtils.assertAsync();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean requiresLoadedWorld() {
        return false;
    }
}
