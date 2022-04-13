package me.illusion.skyblockcore.spigot.data;

import lombok.AccessLevel;
import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandCreateEvent;
import me.illusion.skyblockcore.spigot.event.IslandLoadEvent;
import me.illusion.skyblockcore.spigot.file.SetupData;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

        System.out.println("Loading data for " + getPlayer().getName());

        load("PLAYER", uuid).whenComplete((object, $) -> {
            System.out.println("COMPLETE");
            if (object != null && !(object instanceof PlayerData)) {
                System.err.println("Object is not a player data, send the message below to the developer");
                System.err.println(object.getClass().getName());
                System.err.println(object);
                return;
            }

            data = (PlayerData) object;

            if (data == null) {
                System.out.println("No PlayerData has been found, creating new data");

                data = new PlayerData();
                data.setPlayerId(uuid);
                IslandData islandData = new IslandData(UUID.randomUUID(), uuid);
                islandData.addUser(uuid);
                data.setIslandId(islandData.getId());

                if (main.getSetupData().getServerType() == SetupData.ServerType.ISLAND) {
                    loadIsland(islandData);
                } else {
                    assignRemoteIsland(islandData);
                }

                return;
            }

            System.out.println("Loaded player data, loading island data");

            if (main.getSetupData().getServerType() == SetupData.ServerType.ISLAND) {
                pasteIsland();
            } else {
                load("ISLAND", data.getIslandId()).thenAccept((obj) -> {
                    if (obj != null && !(obj instanceof IslandData)) {
                        System.err.println("Object is not an island data, send the message below to the developer");
                        System.err.println(obj.getClass().getName());
                        System.err.println(obj);
                        return;
                    }

                    IslandData islandData = (IslandData) obj;

                    if (islandData == null) {
                        System.out.println("No IslandData has been found, creating new data");
                        islandData = new IslandData(UUID.randomUUID(), uuid);
                        islandData.addUser(uuid);
                        data.setIslandId(islandData.getId());
                    }

                    assignRemoteIsland(islandData);
                });
            }

        }).exceptionally(throwable -> {
            System.out.println("Failed to load data for " + getPlayer().getName());
            ExceptionLogger.log(throwable);
            return null;
        });
    }

    private void assignRemoteIsland(IslandData islandData) {
        island = main.getIslandManager().loadRemoteIsland(islandData);
    }

    private void pasteIsland() {
        main.getIslandManager().pasteIsland(data.getIslandId(), uuid)
                .thenAccept(island -> {
                    this.island = island;
                    this.islandCenter = island.getCenter();
                    Bukkit.getPluginManager().callEvent(new IslandLoadEvent(island));

                    System.out.println("Loaded island data");

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
    }

    private void loadIsland(IslandData islandData) {
        sync(() -> main.getIslandManager().loadIsland(islandData)
                .thenAccept(island -> {
                    Bukkit.getPluginManager().callEvent(new IslandCreateEvent(island));
                    Bukkit.getPluginManager().callEvent(new IslandLoadEvent(island));
                    this.island = island;
                    this.islandCenter = island.getCenter();

                    new ScheduleBuilder(main)
                            .in(1).seconds()
                            .run(this::teleportToIsland).sync().start();
                }));
    }


    public void teleportToIsland() {
        if (!Bukkit.isPrimaryThread()) {
            System.out.println("Detected async teleport - Calling it sync");
            Bukkit.getScheduler().runTask(main, this::teleportToIsland);
            return;
        }

        island.teleport(getPlayer());

    }

    /**
     * Teleports player to last position
     */
    private void checkTeleport() {
        System.out.println("Teleporting to last location");

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
        System.out.println("Loading " + table + " data for " + Bukkit.getPlayer(uuid).getName());
        return main.getStorageHandler().get(uuid, table);
    }

    // ----- DATA SAVING -----

    /**
     * Saves all the player data, and cleans the island if possible
     */
    public void save() {
        Player player = getPlayer();
        Location loc = player.getLocation();

        data.getLastLocation().update(loc);
        data.getIslandLocation().update(loc);

        island.save().thenRun(() -> {
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
