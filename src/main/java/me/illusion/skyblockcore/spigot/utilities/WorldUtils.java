package me.illusion.skyblockcore.spigot.utilities;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public final class WorldUtils {

    private WorldUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static CompletableFuture<Void> unload(SkyblockPlugin main, String worldName, boolean save) {
        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        main.getWorldManager().whenNextUnload((world) -> latch.countDown(), worldName);

        if (Bukkit.isPrimaryThread())
            Bukkit.unloadWorld(worldName, save);
        else
            Bukkit.getScheduler().runTask(main, () -> Bukkit.unloadWorld(worldName, save));

        return future;
    }

    public static CompletableFuture<Void> unload(SkyblockPlugin main, String worldName) {
        return unload(main, worldName, true);
    }

    public static void deleteRegionFolder(SkyblockPlugin main, String worldName) {
        // Async stuff
        if (Bukkit.isPrimaryThread()) {
            CompletableFuture.runAsync(() -> deleteRegionFolder(main, worldName));
            return;
        }

        World world = Bukkit.getWorld(worldName);

        // Make sure the world is loaded
        if (world != null) {
            unload(main, worldName, false).thenRun(() -> {
                // Delete the region folder
                new ScheduleBuilder(main).in(5).ticks().run(() -> deleteRegionFolder(main, worldName));
            });
            return;
        }

        // Delete the region folder
        File regionFolder = new File(Bukkit.getWorldContainer() + File.separator + worldName + File.separator + "region");

        regionFolder.delete();
        regionFolder.mkdir();
    }

    public static CompletableFuture<World> load(SkyblockPlugin main, String worldName) {
        // Check if the world is already loaded
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            return CompletableFuture.completedFuture(world);
        }

        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<World> future = CompletableFuture.supplyAsync(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return Bukkit.getWorld(worldName);
        });

        main.getWorldManager().whenNextLoad(($) -> latch.countDown(), worldName);

        if (Bukkit.isPrimaryThread())
            Bukkit.createWorld(new WorldCreator(worldName));
        else
            Bukkit.getScheduler().runTask(main, () -> Bukkit.createWorld(new WorldCreator(worldName)));

        return future;
    }

    public static File getRegionFile(File folder, Location location) {
        int x = location.getBlockX() >> 9;
        int z = location.getBlockZ() >> 9;

        return new File(folder + File.separator + "r." + x + "." + z + ".mca");
    }
}
