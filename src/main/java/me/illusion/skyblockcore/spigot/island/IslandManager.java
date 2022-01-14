package me.illusion.skyblockcore.spigot.island;

import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.shared.utilities.FileUtils;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.utilities.LocationUtil;
import me.illusion.skyblockcore.spigot.utilities.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/*
    Island loading is hella weird, so I made spaghetti code

    Sync -> Requests island loading ->

    Async -> Checks if the island is already pasted,
    creates files, if there isn't any pasting required,
    return the already pasted island, otherwise ->

    Sync -> Loads the world

    Async -> Pastes the island (Sync -> Unloads the world, Async -> Replaces files)

    Sync -> Makes sure the world is loaded, returning the island



    Caching: Each island has a UUID, and a respective
    temporary folder, the files get pasted into the cache/<island uuid>/ folder
    and then the files are respectively used on their providers (FAWE pastes directly, .mca unloads the world)

    Island cache folders are deleted upon island deletion.
 */
public class IslandManager {

    private final Map<UUID, Island> islands = new HashMap<>();

    private final SkyblockPlugin main;

    public IslandManager(SkyblockPlugin main) {
        this.main = main;

        File cache = new File(main.getDataFolder(), "cache");

        FileUtils.delete(cache);
        cache.mkdirs();
    }


    void register(Island island) {
        islands.put(island.getData().getId(), island);
    }

    void unregister(Island island) {
        islands.remove(island.getData().getId());
    }

    public Optional<Island> getIslandFromId(UUID islandId) {
        return Optional.ofNullable(islands.get(islandId));
    }

    public Collection<UUID> getLoadedIslandIds() {
        return islands.keySet();
    }

    public boolean isMaxCapacity() {
        return islands.size() <= main.getIslandConfig().getWorldCount();
    }

    /**
     * Gets the island passed a belonging location
     *
     * @param location - The location to match
     * @return NULL if no match is found, Island object otherwise
     */
    public Island getIslandAt(Location location) {
        for (Island island : islands.values())
            if (LocationUtil.locationBelongs(location, island.getPointOne(), island.getPointTwo()))
                return island;
        return null;
    }

    public Island getIsland(UUID islandId) {
        return islands.get(islandId);
    }

    public Island getPlayerIsland(UUID playerId) {
        for (Island island : islands.values())
            if (island.getData().getUsers().contains(playerId))
                return island;

        return null;
    }

    public boolean shouldRemoveIsland(Island island) {
        List<UUID> users = island.getData().getUsers();
        return shouldRemoveIsland(users);
    }

    private boolean shouldRemoveIsland(List<UUID> users) {
        int onlinePlayers = 0;

        for (UUID user : users) {
            if (Bukkit.getPlayer(user) != null)
                onlinePlayers++;
        }

        return onlinePlayers <= 1;
    }

    public CompletableFuture<Island> pasteIsland(UUID islandId, UUID ownerId) {
        return load(islandId)
                .thenApply(data -> {
                    System.out.println("Pasting island " + islandId);
                    if (data == null)
                        data = new IslandData(islandId, ownerId);

                    IslandData islandData = (IslandData) data;

                    try {
                        return loadIsland(islandData).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                }).exceptionally(throwable -> {
                    ExceptionLogger.log(throwable);
                    return null;
                });
    }


    public CompletableFuture<Island> loadIsland(IslandData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long start = System.currentTimeMillis();
                UUID owner = data.getOwner();

                // If any island member is online (island pasted), then we don't need to paste
                boolean paste = shouldRemoveIsland(data.getUsers()); // variable to store pasting

                File folder = new File(main.getDataFolder() + File.separator + "cache" + File.separator + data.getId()); // Create cache folder

                final Island[] island = {null};

                // If we need to paste, we make a latch with 2 uses, one for the island loading, and the second for the world loading
                CountDownLatch latch = new CountDownLatch(paste ? 1 : 0);

                // Pastes island if required
                if (paste) {
                    SerializedFile[] islandFiles = data.getIslandSchematic(); // Obtains original files

                    if (islandFiles == null) {
                        // Assigns default if not found
                        System.out.println("No schematic found for island " + data.getId());
                        islandFiles = SerializedFile.loadArray(main.getStartSchematic());
                    }

                    CompletableFuture<SerializedFile[]> files = createFiles(folder, islandFiles); // Creates cache files

                    files.thenAccept(schematicFiles -> {
                        try {
                            data.setIslandSchematic(schematicFiles); // Updates schematic with cache files

                            String world = main.getWorldManager().assignWorld(data.getId()); // Assigns world

                            island[0] = loadIsland(data, world);// Creates island
                            latch.countDown();
                        } catch (Exception e) {
                            ExceptionLogger.log(e);
                        }

                    });

                } else // If it doesn't need pasting
                    island[0] = getIslandFromId(data.getId()).orElse(null); // Obtains island from ID (loaded by a teammate)

                data.setIsland(island[0]); // Updates island in the island data

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    ExceptionLogger.log(e);
                }

                long end = System.currentTimeMillis();

                Island result = island[0];

                System.out.println("After action report");
                System.out.println("----------------------------------------");
                System.out.println("Time taken: " + (end - start) + "ms");
                System.out.println("Island world: " + (result == null ? "N/A" : result.getWorld()));
                System.out.println("Island world loaded: " + (result == null ? "N/A" : Bukkit.getWorld(result.getWorld()) != null));
                System.out.println("----------------------------------------");

                // --- ENSURE WORLD IS PROPERLY LOADED ---
                if (Bukkit.getWorld(result.getWorld()) == null) {
                    System.out.println("Loading world");
                    CountDownLatch latch2 = new CountDownLatch(1);
                    WorldUtils.load(main, result.getWorld()).thenRun(latch2::countDown);

                    try {
                        latch2.await();
                    } catch (InterruptedException e) {
                        ExceptionLogger.log(e);
                    }
                }

                result.getCenter().setWorld(Bukkit.getWorld(result.getWorld()));

                FileUtils.delete(folder); // Deletes folder
                // ----------------------------------------

                return result;
            } catch (Exception e) {
                ExceptionLogger.log(e);
                return null;
            }
        }).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);

            return null;
        });


    }

    /**
     * Obtains a serialized object
     *
     * @return deserialized object
     */
    private CompletableFuture<Object> load(UUID uuid) {
        return main.getStorageHandler().get(uuid, "ISLAND");
    }

    /**
     * Pastes an island
     *
     * @param data  - The island data, used in the island object
     * @param world - The world to paste the island on
     * @return island object
     */
    private Island loadIslandLoadedWorld(IslandData data, World world) {
        try {
            CountDownLatch latch = new CountDownLatch(1);

            Location center = world.getSpawnLocation();

            System.out.println(world.getName() + " spawn location: " + center);
            int offset = main.getIslandConfig().getOverworldSettings().getMaxSize() >> 1;

            Location one = center.clone().add(-offset, -128, -offset);
            Location two = center.clone().add(offset, 128, offset);

            main.getPastingHandler().paste(data.getIslandSchematic(), center).thenRun(latch::countDown);


            latch.await();

            return new Island(main, one, two, center, data, world.getName());
        } catch (Exception e) {
            ExceptionLogger.log(e);
            return null;
        }
    }

    private Island loadIslandUnloadedWorld(IslandData data, String worldName) {
        try {
            CountDownLatch latch = new CountDownLatch(1);

            Vector centerPoint = main.getIslandConfig().getSpawnPoint();

            System.out.println(worldName + " spawn location: " + centerPoint);
            int offset = main.getIslandConfig().getOverworldSettings().getMaxSize() >> 1;

            main
                    .getPastingHandler()
                    .paste(data.getIslandSchematic(), worldName, centerPoint)
                    .thenRun(() ->
                            WorldUtils.load(main, worldName))
                    .thenRun(latch::countDown);

            latch.await();

            World world = Bukkit.getWorld(worldName);
            Location center = centerPoint.toLocation(world);

            Location one = center.clone().add(-offset, -128, -offset);
            Location two = center.clone().add(offset, 128, offset);

            return new Island(main, one, two, center, data, worldName);

        } catch (Exception e) {
            ExceptionLogger.log(e);
            return null;
        }
    }

    private Island loadIsland(IslandData data, String worldName) {
        boolean requiresLoad = main.getPastingHandler().requiresLoadedWorld();

        if (requiresLoad) {
            return loadIslandLoadedWorld(data, Bukkit.getWorld(worldName));
        }

        return loadIslandUnloadedWorld(data, worldName);
    }

    /**
     * Creates cache island files
     *
     * @param folder - The folder where to write the files do (default - cache)
     * @param files  - The files to put
     * @return The new renamed files
     */
    // I don't care that files are not created if they already exist, just making sure
    // I also can't get around the rawtypes warning without any other warning, I need an array of completablefutures, and they're generic type classes
    @SuppressWarnings({"ResultOfMethodCallIgnored", "rawtypes"})
    private CompletableFuture<SerializedFile[]> createFiles(File folder, SerializedFile... files) {
        return CompletableFuture.supplyAsync(() -> {
            SerializedFile[] copyArray = new SerializedFile[files.length];

            try {
                folder.getParentFile().mkdirs(); // Create parent folder if it doesn't exist
                folder.mkdir(); // Create folder if it doesn't exist

                CountDownLatch latch = new CountDownLatch(1); // Used to wait for the files to be written async

                CompletableFuture[] futures = new CompletableFuture[files.length]; // Used to manage all the files being written

                for (int index = 0; index < files.length; index++) { // Loops through all the serialized files
                    SerializedFile file = files[index].copy(); // Copies the file, so we don't modify the original

                    int finalI = index; // Java limitation REEEEEEEEEEE

                    futures[index] = file.getFile() // Obtain a future of the file
                            .whenComplete((realFile, throwable) -> { // Which is then used to change internal data
                                try {
                                    if (throwable != null)
                                        throwable.printStackTrace();


                                    file.setFile(new File(folder, realFile.getName())); // Change the file to the new location
                                    file.save(); // Save the file

                                    copyArray[finalI] = file; // Add the file to the copy array
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }

                            });
                }

                CompletableFuture.allOf(futures).whenComplete((v, throwable) -> { // Waits for all the files to be written
                    if (throwable != null) // If there was an error
                        throwable.printStackTrace(); // Prints the error

                    latch.countDown(); // Allows the method to finish and return
                }).join();

                try {
                    latch.await(); // Waits for the files to be written
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }


            return copyArray; // Returns the copy array
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public void deleteIsland(UUID islandId) {
        Island island = getIsland(islandId);

        File folder = new File(main.getDataFolder() + File.separator + "cache" + File.separator + islandId); // Create cache folder

        if (shouldRemoveIsland(island))
            island.cleanIsland();

        System.out.println("Attempting to delete island files");

        FileUtils.delete(folder); // Delete the folder
    }


}
