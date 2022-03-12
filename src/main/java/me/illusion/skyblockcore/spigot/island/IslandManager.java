package me.illusion.skyblockcore.spigot.island;

import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.shared.utilities.FileUtils;
import me.illusion.skyblockcore.shared.utilities.Reference;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandUnloadEvent;
import me.illusion.skyblockcore.spigot.utilities.LocationUtil;
import me.illusion.skyblockcore.spigot.utilities.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private final Map<UUID, CompletableFuture<Island>> loadingIslands = new HashMap<>();

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
                        ExceptionLogger.log(e);
                    }

                    return null;
                }).exceptionally(throwable -> {
                    ExceptionLogger.log(throwable);
                    return null;
                });
    }


    public CompletableFuture<Island> loadIsland(IslandData data) {
        if (loadingIslands.containsKey(data.getId()))
            return loadingIslands.get(data.getId());

        CompletableFuture<Island> future = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            UUID islandId = data.getId();

            // If any island member is online (island pasted), then we don't need to paste
            boolean paste = shouldRemoveIsland(data.getUsers()); // variable to store pasting

            if (!paste) {
                return main.getIslandManager().getIsland(islandId);
            }

            File folder = new File(main.getDataFolder() + File.separator + "cache" + File.separator + islandId); // Create cache folder

            final Reference<Island> islandReference = new Reference<>();

            // Pastes island if required
            SerializedFile[] islandFiles = data.getIslandSchematic(); // Obtains original files

            if (islandFiles == null) {
                // Assigns default if not found
                System.out.println("No schematic found for island " + islandId);
                islandFiles = SerializedFile.loadArray(main.getStartSchematic());
            }

            createFiles(folder, islandFiles) // Creates cache files
                    .thenAccept(schematicFiles -> {
                        data.setIslandSchematic(schematicFiles); // Updates schematic with cache files

                        String world = main.getWorldManager().assignWorld(islandId); // Assigns world

                        islandReference.set(loadIsland(data, world)); // Creates island
                    }).exceptionally(throwable -> {
                        ExceptionLogger.log(throwable);
                        return null;
                    }).join();

            long end = System.currentTimeMillis();

            Island result = islandReference.get();
            data.setIsland(result); // Updates island in the island data

            if (result == null) {
                System.out.println("Island " + islandId + " failed to load");
                return null;
            }

            World islandWorld = Bukkit.getWorld(result.getWorld());

            System.out.println("After action report");
            System.out.println("----------------------------------------");
            System.out.println("Time taken: " + (end - start) + "ms");
            System.out.println("Island world: " + result.getWorld());
            System.out.println("Island world loaded: " + (islandWorld != null));
            System.out.println("----------------------------------------");

            // --- ENSURE WORLD IS PROPERLY LOADED ---
            if (islandWorld == null) {
                System.out.println("Loading world");
                islandWorld = WorldUtils.load(main, result.getWorld()).join();
            }

            result.getCenter().setWorld(islandWorld);

            FileUtils.delete(folder); // Deletes folder
            // ----------------------------------------

            return result;

        }).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });

        loadingIslands.put(data.getId(), future);
        return future;
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
            Location center = world.getSpawnLocation();

            System.out.println(world.getName() + " spawn location: " + center);
            int offset = main.getIslandConfig().getOverworldSettings().getMaxSize() >> 1;

            Location one = center.clone().add(-offset, -128, -offset);
            Location two = center.clone().add(offset, 128, offset);

            WorldUtils.assertAsync();
            main.getPastingHandler().paste(data.getIslandSchematic(), center).join();

            return new Island(main, one, two, center, data, world.getName());
        } catch (Exception e) {
            ExceptionLogger.log(e);
            return null;
        }
    }

    private Island loadIslandUnloadedWorld(IslandData data, String worldName) {
        try {
            Vector centerPoint = main.getIslandConfig().getSpawnPoint();

            System.out.println(worldName + " spawn location: " + centerPoint);
            int offset = main.getIslandConfig().getOverworldSettings().getMaxSize() >> 1;

            main
                    .getPastingHandler()
                    .paste(data.getIslandSchematic(), worldName, centerPoint)
                    .thenRun(() ->
                            WorldUtils.load(main, worldName)
                    )
                    .join();

            WorldUtils.assertAsync();

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


                CompletableFuture[] futures = new CompletableFuture[files.length]; // Used to manage all the files being written

                for (int index = 0; index < files.length; index++) { // Loops through all the serialized files
                    SerializedFile file = files[index].copy(); // Copies the file, so we don't modify the original

                    int finalI = index; // Java limitation REEEEEEEEEEE

                    futures[index] = file.getFile() // Obtain a future of the file
                            .whenComplete((realFile, throwable) -> { // Which is then used to change internal data
                                try {
                                    if (throwable != null)
                                        ExceptionLogger.log(throwable);


                                    file.setFile(new File(folder, realFile.getName())); // Change the file to the new location
                                    file.save(); // Save the file

                                    copyArray[finalI] = file; // Add the file to the copy array
                                } catch (Exception exception) {
                                    ExceptionLogger.log(exception);
                                }

                            });
                }

                WorldUtils.assertAsync();
                CompletableFuture.allOf(futures).exceptionally((throwable) -> { // Waits for all the files to be written
                    if (throwable != null) // If there was an error
                        ExceptionLogger.log(throwable); // Prints the error

                    return null;
                }).join();


            } catch (Exception exception) {
                ExceptionLogger.log(exception);
            }

            return copyArray; // Returns the copy array
        }).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });
    }

    public void deleteIsland(UUID islandId) {
        Island island = getIsland(islandId);

        Bukkit.getPluginManager().callEvent(new IslandUnloadEvent(island));

        File folder = new File(main.getDataFolder() + File.separator + "cache" + File.separator + islandId); // Create cache folder

        if (shouldRemoveIsland(island))
            island.cleanIsland();

        System.out.println("Attempting to delete island files");

        FileUtils.delete(folder); // Delete the folder
    }


}
