package me.illusion.skyblockcore.spigot.island;

import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.utilities.LocationUtil;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class IslandManager {

    private final Map<UUID, Island> islands = new HashMap<>();

    private final SkyblockPlugin main;

    public IslandManager(SkyblockPlugin main) {
        this.main = main;
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

    public CompletableFuture<Island> pasteIsland(UUID islandId) {
        return load(islandId)
                .thenApply(data -> {
                    if (data == null)
                        return null;

                    IslandData islandData = (IslandData) data;

                    try {
                        return loadIsland(islandData).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                }).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }


    public CompletableFuture<Island> loadIsland(IslandData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // If any island member is online (island pasted), then we don't need to paste
                boolean paste = shouldRemoveIsland(data.getUsers()); // variable to store pasting

                File folder = new File(main.getDataFolder() + File.separator + "cache" + File.separator + data.getId()); // Create cache folder

                final Island[] island = {null};

                CountDownLatch latch = new CountDownLatch(paste ? 1 : 0);

                System.out.println("Pasting required? " + paste);
                // Pastes island if required
                if (paste) {
                    SerializedFile[] islandFiles = data.getIslandSchematic(); // Obtains original files

                    if (islandFiles == null) {
                        // Assigns default if not found
                        islandFiles = SerializedFile.loadArray(main.getStartSchematic());
                    }

                    CompletableFuture<SerializedFile[]> files = createFiles(folder, islandFiles); // Creates cache files

                    files.thenAccept(schematicFiles -> {
                        data.setIslandSchematic(schematicFiles); // Updates schematic with cache files

                        String world = main.getWorldManager().assignWorld(); // Assigns world

                        Bukkit.getScheduler().runTask(main, () -> {
                            island[0] = loadIsland(data, new WorldCreator(world).generator("Skyblock").createWorld()); // Loads island

                            new ScheduleBuilder(main)
                                    .in(20).ticks()
                                    .run(latch::countDown)
                                    .async()
                                    .start();
                        });


                    });

                } else // If it doesn't need pasting
                    island[0] = getIslandFromId(data.getId()).orElse(null); // Obtains island from ID (loaded by a teammate)

                data.setIsland(island[0]); // Updates island in the island data

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return island[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
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
    private Island loadIsland(IslandData data, World world) {
        Location center = world.getSpawnLocation();
        int offset = main.getIslandConfig().getOverworldSettings().getMaxSize() >> 1;

        Location one = center.clone().add(-offset, -128, -offset);
        Location two = center.clone().add(offset, 128, offset);

        main.getPastingHandler().paste(data.getIslandSchematic(), center);

        return new Island(main, one, two, center, data, world.getName());
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

                System.out.println("Created folders");
                CountDownLatch latch = new CountDownLatch(1); // Used to wait for the files to be written async

                System.out.println("Created new latch");

                CompletableFuture[] futures = new CompletableFuture[files.length]; // Used to manage all the files being written
                System.out.println("Created an array of futures");

                for (int index = 0; index < files.length; index++) { // Loops through all the serialized files
                    SerializedFile file = files[index].copy(); // Copies the file, so we don't modify the original
                    System.out.println("Copied file at index" + index);

                    int finalI = index; // Java limitation REEEEEEEEEEE

                    System.out.println("Creating future");
                    futures[index] = file.getFile() // Obtain a future of the file
                            .whenComplete((realFile, throwable) -> { // Which is then used to change internal data
                                System.out.println("Obtained real file");
                                try {
                                    if (throwable != null)
                                        throwable.printStackTrace();


                                    System.out.println("Set new file");
                                    file.setFile(new File(folder, realFile.getName())); // Change the file to the new location

                                    System.out.println("Saved file");
                                    file.save(); // Save the file

                                    System.out.println("Updated array");
                                    copyArray[finalI] = file; // Add the file to the copy array
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }

                            });
                }

                System.out.println("Running all futures");
                CompletableFuture.allOf(futures).whenComplete((v, throwable) -> { // Waits for all the files to be written
                    if (throwable != null) // If there was an error
                        throwable.printStackTrace(); // Prints the error

                    System.out.println("Counting down latch");
                    latch.countDown(); // Allows the method to finish and return
                }).join();

                try {
                    System.out.println("Awaiting latch");
                    latch.await(); // Waits for the files to be written
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Latch unlocked");
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

        folder.delete(); // Delete the folder
    }

}
