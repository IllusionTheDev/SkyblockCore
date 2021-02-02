package me.illusion.skyblockcore.spigot.data;

import com.google.common.io.Files;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.sql.SQLSerializer;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.sql.serialized.SerializedLocation;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    @SneakyThrows
    private void load() {
        Player p = getPlayer();

        load("PLAYER", uuid).whenComplete((object, thr) -> {
            data = (PlayerData) object;

            if (data == null) {
                data = new PlayerData();
                IslandData islandData = new IslandData(UUID.randomUUID(), uuid, new ArrayList<>());
                data.getInventory().updateArray(p.getInventory().getContents());
                sync(() -> loadIsland(islandData));
            } else {
                load("ISLAND", data.getIslandId()).whenComplete((islandObject, thr2) ->
                        sync(() -> loadIsland((IslandData) islandObject)));
            }
        });
        /*
        Player p = getPlayer(); // Obtain the Bukkit player
        data = (PlayerData) load("PLAYER", uuid); // Load the player data
        IslandData islandData; // Island data variable

        boolean paste = true; // Default pasting as true

        File folder = new File(main.getDataFolder() + File.separator + "cache"); // Create cache folder

        if (data == null) { // If the player data is null (player never joined before)
            data = new PlayerData(); // Create new data
            islandData = new IslandData(UUID.randomUUID(), uuid, new ArrayList<>()); // Assign new island data
            data.getInventory().updateArray(p.getInventory().getContents()); // Update serialized contents
        } else { // If the player joined before
            islandData = (IslandData) load("ISLAND", data.getIslandId()); // Load island data

            // Obtain island members
            List<UUID> members = islandData.getUsers();

            // If any island member is online (island pasted)
            for (UUID uuid : members)
                if (!uuid.equals(this.uuid) && Bukkit.getPlayer(uuid) != null) {
                    paste = false;
                    break;
                }
        }

        // Obtain island UUID
        UUID uuid = islandData.getId();

        // Premake island cache files
        if (paste) {
            File[] islandFiles = islandData.getIslandSchematic();

            if (islandFiles == null)
                islandFiles = main.getStartSchematic();

            File[] files = createFiles(uuid, folder, islandFiles);

            islandData.setIslandSchematic(files);
        }

        // Set the island UUID in the player data
        data.setIslandId(uuid);

        // Final copy of paste
        boolean finalPaste = paste;

        // Back to sync
        Bukkit.getScheduler().runTask(main, () -> {
            // Paste the island
            if (finalPaste) {
                String world = main.getWorldManager().assignWorld();
                island = loadIsland(islandData, new WorldCreator(world).createWorld());
            }

            // Update the island internally (island not serialized)
            islandData.setIsland(island);

            // Obtain last player location
            SerializedLocation last = data.getLastLocation();

            // Assign player location if not found
            if (last.getLocation() == null) {
                Location loc = islandCenter;
                last.update(loc);
                data.getIslandLocation().update(loc);
            }

            // Update XP values (default 0)
            p.setExp(data.getExperience());
            p.setLevel(data.getExperienceLevel());

            // Teleports
            checkTeleport();
            // Updates inventory
            updateInventory();
        });
         */
    }

    private void loadIsland(IslandData islandData) {
        Player p = getPlayer();

        data.setIslandId(islandData.getId());

        boolean paste = true;

        List<UUID> members = islandData.getUsers();

        // If any island member is online (island pasted)
        for (UUID uuid : members)
            if (!uuid.equals(this.uuid) && Bukkit.getPlayer(uuid) != null) {
                paste = false;
                break;
            }

        File folder = new File(main.getDataFolder() + File.separator + "cache"); // Create cache folder

        if (paste) {
            File[] islandFiles = islandData.getIslandSchematic();

            if (islandFiles == null)
                islandFiles = main.getStartSchematic();

            File[] files = createFiles(uuid, folder, islandFiles);

            islandData.setIslandSchematic(files);

            String world = main.getWorldManager().assignWorld();

            System.out.println("Assigned world " + world + " for player " + p.getName());
            island = loadIsland(islandData, new WorldCreator(world).generator(main.getEmptyWorldGenerator()).createWorld());
        } else
            island = main.getIslandManager().getIslandFromId(islandData.getId()).orElse(null);

        islandData.setIsland(island);

        SerializedLocation last = data.getLastLocation();

        // Assign player location if not found
        if (last.getLocation() == null) {
            Location loc = islandCenter;
            last.update(loc);
            data.getIslandLocation().update(loc);
        }

        // Update XP values (default 0)
        p.setExp(data.getExperience());
        p.setLevel(data.getExperienceLevel());

        // Teleports
        checkTeleport();
        // Updates inventory
        updateInventory();
    }

    private File[] createFiles(UUID id, File folder, File... files) {
        File[] copyArray = new File[files.length];

        folder.mkdir();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            File copy = new File(folder, id + "_" + i + "." + FilenameUtils.getExtension(file.getName()));

            copyArray[i] = copy;

            if (file.equals(copy))
                continue;

            try {
                if (!copy.exists())
                    copy.createNewFile();
                Files.copy(file, copy);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return copyArray;
    }

    /**
     * Teleports the player to its island relative position
     * If the location isn't on the island world, the location is no longer relative
     */
    private void checkTeleport() {
        System.out.println("Teleporting");

        Player p = getPlayer();
        Location lastLoc = data.getLastLocation().getLocation();

        World world = Bukkit.getWorld(island.getWorld());

        String worldName = world.getName();
        String schematicName = lastLoc.getWorld().getName();

        if (worldName.equalsIgnoreCase(schematicName))
            p.teleport(data.getIslandLocation().getLocation().add(islandCenter.getX(), 0, islandCenter.getZ()));
        else
            p.teleport(lastLoc);
    }

    /**
     * Pastes an island
     *
     * @param data  - The island data, used in the island object
     * @param world - The world to paste the island on
     * @return island object
     */
    private Island loadIsland(IslandData data, World world) {
        Location center = new Location(world, 0, 128, 0);
        int offset = main.getIslandConfig().getOverworldSettings().getMaxSize() >> 1;

        Location one = center.add(-offset, 128, -offset);
        Location two = center.add(offset, -128, offset);

        main.getPastingHandler().paste(data.getIslandSchematic(), center);

        islandCenter = center;
        System.out.println("Pasted Island.");

        return new Island(main, one, two, center, data, world.getName());
    }

    /**
     * Obtains a serialized object
     *
     * @return deserialized object
     */
    private CompletableFuture<Object> load(String table, UUID uuid) {
        return SQLSerializer.deserialize(main.getMySQLConnection(), uuid, table);
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
        data.getInventory().updateArray(p.getInventory().getContents());
        island.save(() -> {
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

            for (File file : island.getData().getIslandSchematic())
                if (file != null && file.exists())
                    file.delete();

        });


    }

    /**
     * Serializes the object and sets the serialized ID into the SQL statement
     *
     * @param object - The object to serialize
     */
    private void saveObject(UUID uuid, Object object) {
        SQLSerializer.serialize(main.getMySQLConnection(), uuid, object, "PLAYER");
    }

    // ----- DATA POST-LOAD -----

    /**
     * Loads the inventory from serialized data
     */
    private void updateInventory() {
        if (data == null)
            getPlayer().getInventory().clear();

        getPlayer().getInventory().setContents(data.getInventory().getArray());
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(main, runnable);
    }

}
