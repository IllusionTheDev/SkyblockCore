package me.illusion.skyblockcore.spigot.data;

import lombok.AccessLevel;
import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.utilities.Log;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.sql.serialized.SerializedLocation;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkyblockPlayer {

    @Getter(AccessLevel.NONE)
    private final SkyblockPlugin main;

    private final UUID uuid;
    private PlayerData data;

    private Location islandCenter;
    private Island island;

    public SkyblockPlayer(SkyblockPlugin main, UUID uuid) {
        this.main = main;
        this.uuid = uuid;

        load();

        main.getPlayerManager().register(uuid, this);
    }

    /**
     * Gets the Bukkit Player
     *
     * @return null if offline, Bukkit player otherwise
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    // ----- DATA LOADING -----

    /**
     * Loads the player and island data
     */
    private void load() {

        Log.info("Loading data for " + getPlayer().getName());

        load("PLAYER", uuid).whenComplete((object, $) -> {
            Log.info("COMPLETE");
            if (object != null && !(object instanceof PlayerData)) {
                System.err.println("Object is not a player data, send the message below to the developer");
                System.err.println(object.getClass().getName());
                System.err.println(object);
                return;
            }

            data = (PlayerData) object;

            if (data == null) {
                Log.info("No PlayerData has been found, creating new data");

                data = new PlayerData();
                IslandData islandData = new IslandData(UUID.randomUUID(), uuid);
                islandData.addUser(uuid);
                sync(() -> main.getIslandManager().loadIsland(islandData)
                        .thenAccept(island -> {
                            this.island = island;
                            this.islandCenter = island.getCenter();

                            data.setIslandId(island.getData().getId());

                            new ScheduleBuilder(main)
                                    .in(1).seconds()
                                    .run(this::teleportToIsland).sync().start();
                        }));
                return;
            }

            Log.info("Loaded player data, loading island data");
            main.getIslandManager().pasteIsland(data.getIslandId(), uuid)
                    .thenAccept(island -> {
                        this.island = island;
                        this.islandCenter = island.getCenter();
                        Log.info("Loaded island data");


                        sync(() -> {
                            SerializedLocation last = data.getLastLocation(); // Obtains last location

                            // Assign player location if not found
                            if (last.getLocation() == null) {
                                teleportToIsland();
                                return;
                            }

                            // Teleports
                            checkTeleport();
                        });

                    });

        }).exceptionally(throwable -> {
            Log.info("Failed to load data for " + getPlayer().getName());
            ExceptionLogger.log(throwable);
            return null;
        });
    }


    public void teleportToIsland() {
        if (!Bukkit.isPrimaryThread()) {
            Log.info("Detected async teleport - Calling it sync");
            Bukkit.getScheduler().runTask(main, this::teleportToIsland);
            return;
        }

        World world = islandCenter.getWorld();

        if (world == null) {
            Log.info("Teleporting - World is null");
            islandCenter.setWorld(Bukkit.getWorld(island.getWorld()));
        }

        Log.info("Teleporting to island");
        Log.info(islandCenter);

        if (!islandCenter.getChunk().isLoaded())
            islandCenter.getChunk().load();

        Player player = getPlayer();
        player.teleport(islandCenter);
        data.getIslandLocation().update(islandCenter);

    }

    /**
     * Teleports player to last position
     */
    private void checkTeleport() {
        Log.info("Teleporting to last location");

        Player player = getPlayer();
        SerializedLocation last = data.getLastLocation();

        String worldName = last.getWorldName();
        Location location = last.getLocation();

        if (worldName.startsWith("skyblockworld"))
            location.setWorld(islandCenter.getWorld());

        player.teleport(location);
    }


    /**
     * Obtains a serialized object
     *
     * @return deserialized object
     */
    private CompletableFuture<Object> load(String table, UUID uuid) {
        Log.info("Loading " + table + " data for " + Bukkit.getPlayer(uuid).getName());
        return main.getStorageHandler().get(uuid, table);
    }

    // ----- DATA SAVING -----

    /**
     * Saves all the player data, and cleans the island if possible
     */
    public void save() {
        Player player = getPlayer();
        Location loc = player.getLocation();

        data.setExperience(player.getExp());
        data.setExperienceLevel(player.getLevel());
        data.getLastLocation().update(loc);
        data.getIslandLocation().update(loc);

        island.save(() -> {
            Log.info("Saved island data");
            saveObject(uuid, data);
            main.getIslandManager().deleteIsland(island.getData().getId());
        });


    }

    /**
     * Serializes the object and sets the serialized ID into the SQL statement
     *
     * @param object - The object to serialize
     */
    private void saveObject(UUID uuid, Object object) {
        main.getStorageHandler().save(uuid, object, "PLAYER");
    }

    // ----- DATA POST-LOAD -----

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(main, runnable);
    }

}
