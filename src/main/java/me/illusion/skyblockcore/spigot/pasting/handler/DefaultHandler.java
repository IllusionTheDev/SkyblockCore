package me.illusion.skyblockcore.spigot.pasting.handler;

import com.google.common.io.Files;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import me.illusion.skyblockcore.spigot.utilities.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class DefaultHandler implements PastingHandler {

    private final SkyblockPlugin main;
    private String extension;

    public DefaultHandler(SkyblockPlugin main) {
        this.main = main;
    }

    private CompletableFuture<Void> paste(SerializedFile serializedFile, String name) {
        return serializedFile.getFile().thenAccept((file) -> {
            System.out.println("Checking extension");
            if (extension == null)
                extension = getExtension(file.getName());

            // Obtain the region folder for the world
            File regionFolder = new File(Bukkit.getWorldContainer() + File.separator + name + File.separator + "region");

            System.out.println("Unloading");

            deleteWorldFolder(regionFolder, file);
        });
        // Re-load world
    }

    private void deleteWorldFolder(File regionFolder, File finalFile) {
        regionFolder.delete();
        regionFolder.mkdir();

        // Create the new file
        File newFile = new File(regionFolder, finalFile.getName());

        try {
            // Create the region file
            newFile.createNewFile();

            System.out.println("Copied fake world");
            // Copy the file
            Files.copy(finalFile, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> paste(SerializedFile[] file, Location loc) {
        String name = loc.getWorld().getName();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CountDownLatch mainLatch = new CountDownLatch(file.length);

        WorldUtils
                .unload(main, name)
                .thenRun(() -> {
                    for (SerializedFile f : file)
                        futures.add(paste(f, name));

                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenAccept(($$) -> {
                        System.out.println("Done pasting");
                        mainLatch.countDown();
                    });
                });

        return CompletableFuture.runAsync(() -> {
            wait(mainLatch);

            CountDownLatch latch = new CountDownLatch(1);

            WorldUtils.load(main, name).thenRun(latch::countDown);

            wait(latch);
        });


    }

    @Override
    public void save(Island island, Consumer<SerializedFile[]> action) {
        World world = Bukkit.getWorld(island.getWorld());

        main.getWorldManager().whenNextSave(($) -> {
            File regionFolder = new File(world.getWorldFolder() + File.separator + "region");

            List<SerializedFile> list = new ArrayList<>();

            Location one = island.getPointOne();
            Location two = island.getPointTwo();

            int xOne = one.getBlockX() >> 9;
            int zOne = one.getBlockZ() >> 9;
            int xTwo = two.getBlockX() >> 9;
            int zTwo = two.getBlockZ() >> 9;

            for (int x = xOne; x <= xTwo; x++)
                for (int z = zOne; z <= zTwo; z++)
                    list.add(new SerializedFile(new File(regionFolder, "r." + x + "." + z + "." + extension)));

            action.accept(list.toArray(new SerializedFile[]{}));
        }, world.getName());

        world.save();


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
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
