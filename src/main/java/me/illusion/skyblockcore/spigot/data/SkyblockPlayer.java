package me.illusion.skyblockcore.spigot.data;

import lombok.AccessLevel;
import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.sql.serialized.SerializedLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
                sync(() -> main.getIslandManager().loadIsland(islandData));

                return;
            }

            System.out.println("Loaded player data, loading island data");
            main.getIslandManager().pasteIsland(data.getIslandId())
                    .thenAccept(island -> {
                        this.island = island;
                        this.islandCenter = island.getCenter();
                        System.out.println("Loaded island data");


                        sync(() -> {
                            checkTeleport();
                            SerializedLocation last = data.getLastLocation(); // Obtains last location

                            // Assign player location if not found
                            if (last.getLocation() == null) {
                                Location loc = islandCenter;
                                last.update(loc);
                                data.getIslandLocation().update(loc);
                            }

                            // Teleports
                            checkTeleport();
                        });

                    });

        });
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
            System.out.println("Saving data");
            CompletableFuture.runAsync(() -> saveObject(uuid, data));

            boolean delete = true;

            for (UUID uuid : island.getData().getUsers()) {
                if (uuid.equals(this.uuid))
                    continue;
                if (Bukkit.getPlayer(uuid) == null)
                    continue;
                delete = false;
                break;
            }

            if (delete)
                island.cleanIsland();

            System.out.println("Attempting to delete island files");

            for (SerializedFile serializedFile : island.getData().getIslandSchematic()) {
                File file = null;
                try {
                    file = serializedFile.getFile().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (file != null && file.exists())
                    file.delete();
            }

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
