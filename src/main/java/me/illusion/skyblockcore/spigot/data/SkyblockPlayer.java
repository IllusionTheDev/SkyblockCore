package me.illusion.skyblockcore.spigot.data;

import lombok.AccessLevel;
import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.sql.serialized.SerializedLocation;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
            data = (PlayerData) object;

            if (data == null) {
                System.out.println("No PlayerData has been found, creating new data");

                data = new PlayerData();
                IslandData islandData = new IslandData(UUID.randomUUID(), uuid, new ArrayList<>());
                sync(() -> main.getIslandManager().loadIsland(islandData)
                        .thenAccept(island -> {
                            System.out.println("Pasted island with id " + island.getData().getId());
                            this.island = island;
                            this.islandCenter = island.getCenter();

                            data.setIslandId(island.getData().getId());

                            new ScheduleBuilder(main)
                                    .in(1).seconds()
                                    .run(this::teleportToIsland).sync().start();
                        }));
                return;
            }

            System.out.println("Loaded player data, loading island data");
            main.getIslandManager().pasteIsland(data.getIslandId())
                    .thenAccept(island -> {
                        this.island = island;
                        this.islandCenter = island.getCenter();
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

        });
    }


    public void teleportToIsland() {
        if (!Bukkit.isPrimaryThread()) {
            System.out.println("Detected async teleport - Calling it sync");
            Bukkit.getScheduler().runTask(main, this::teleportToIsland);
            return;
        }

        islandCenter.getChunk().load();
        Player p = getPlayer();
        p.teleport(islandCenter);
        data.getIslandLocation().update(islandCenter);

    }

    /**
     * Teleports player to last position
     */
    private void checkTeleport() {
        System.out.println("Teleporting");

        Player p = getPlayer();
        Location lastLoc = data.getLastLocation().getLocation();
        p.teleport(lastLoc);
    }


    /**
     * Obtains a serialized object
     *
     * @return deserialized object
     */
    private CompletableFuture<Object> load(String table, UUID uuid) {
        return main.getStorageHandler().get(uuid, table);
    }

    // ----- DATA SAVING -----

    /**
     * Saves all the player data, and cleans the island if possible
     */
    public void save() {
        Player p = getPlayer();
        Location loc = p.getLocation();

        data.setExperience(p.getExp());
        data.setExperienceLevel(p.getLevel());
        data.getLastLocation().update(loc);
        data.getIslandLocation().update(loc);

        island.save(() -> {
            System.out.println("Saved island data");
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
