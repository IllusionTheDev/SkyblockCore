package me.illusion.skyblockcore.spigot.utilities;

import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.shared.utilities.FileUtils;
import me.illusion.skyblockcore.shared.utilities.Latch;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class WorldUtils {

    private static final int REDUCE_TO_CHUNK = 9;

    private WorldUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static CompletableFuture<Void> unload(SkyblockPlugin main, String worldName, boolean save) {
        Latch latch = new Latch();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                ExceptionLogger.log(e);
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

        FileUtils.delete(regionFolder);
    }

    public static CompletableFuture<World> load(SkyblockPlugin main, String worldName) {
        // Check if the world is already loaded
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            return CompletableFuture.completedFuture(world);
        }

        CompletableFuture<World> future = new CompletableFuture<>();

        if (Bukkit.isPrimaryThread())
            future.complete(Bukkit.createWorld(new WorldCreator(worldName)));
        else
            Bukkit.getScheduler().runTask(main, () -> future.complete(Bukkit.createWorld(new WorldCreator(worldName))));

        return future;
    }

    public static File getRegionFile(File folder, Location location) {
        int x = location.getBlockX() >> 9;
        int z = location.getBlockZ() >> 9;

        return new File(folder + File.separator + "r." + x + "." + z + ".mca");
    }

    public static File[] getAllFilesBetween(File folder, Location one, Location two) {
        // get min and max locations
        int minX = Math.min(one.getBlockX(), two.getBlockX()) >> REDUCE_TO_CHUNK;
        int minZ = Math.min(one.getBlockZ(), two.getBlockZ()) >> REDUCE_TO_CHUNK;

        int maxX = Math.max(one.getBlockX(), two.getBlockX()) >> REDUCE_TO_CHUNK;
        int maxZ = Math.max(one.getBlockZ(), two.getBlockZ()) >> REDUCE_TO_CHUNK;

        File[] files = new File[(maxX - minX + 1) * (maxZ - minZ + 1)];

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                File file = new File(folder, "r." + x + "." + z + ".mca");

                files[(x - minX) * (maxZ - minZ + 1) + (z - minZ)] = file;
            }
        }

        return files;
    }

    public static void save(SkyblockPlugin main, String worldName, Consumer<World> worldAction) {
        // mandatory sync
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(main, () -> save(main, worldName, worldAction));
            return;
        }

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return;
        }

        main.getWorldManager().whenNextSave(worldAction, worldName);
        world.save();
    }

}
