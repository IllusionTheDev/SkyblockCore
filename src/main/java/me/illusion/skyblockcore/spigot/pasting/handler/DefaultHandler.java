package me.illusion.skyblockcore.spigot.pasting.handler;

import com.google.common.io.Files;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class DefaultHandler implements PastingHandler {

    private final SkyblockPlugin main;
    private String extension;

    public DefaultHandler(SkyblockPlugin main) {
        this.main = main;
    }

    private CompletableFuture<Void> paste(SerializedFile serializedFile, String name) {
        return CompletableFuture.runAsync(() -> {
            File file = null;
            try {
                System.out.println("Getting region file");
                file = serializedFile.getFile().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

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

        Bukkit.unloadWorld(name, false);

        main.getWorldManager().whenNextUnload(($) -> {
            for (SerializedFile f : file)
                futures.add(paste(f, name));
        }, name);


        return CompletableFuture.runAsync(() -> {
            CountDownLatch latch = new CountDownLatch(1);

            System.out.println("Waiting for all futures to finish");
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            System.out.println("Load");
            Bukkit.getScheduler().runTask(main, () -> new WorldCreator(name).generator("Skyblock").type(WorldType.NORMAL).createWorld());
            main.getWorldManager().whenNextLoad(($) -> {
                System.out.println("Loaded");
                latch.countDown();
            }, name);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }

    @Override
    public void save(Island island, Consumer<SerializedFile[]> action) {
        World world = Bukkit.getWorld(island.getWorld());
        world.save();

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

    }

    @Override
    public PastingType getType() {
        return PastingType.DEFAULT;
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return filename.substring(index + 1);
    }
}
