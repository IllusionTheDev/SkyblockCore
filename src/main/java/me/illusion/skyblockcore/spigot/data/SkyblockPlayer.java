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
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
                sync(() -> loadIsland(islandData));
                return;
            }

            System.out.println("Loaded player data, loading island data");
            load("ISLAND", data.getIslandId()).whenComplete((islandObject, $$) -> sync(() -> loadIsland((IslandData) islandObject)));

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

    /**
     * Loads island and finalizes player data
     *
     * @param islandData - The island data to load from
     */
    private void loadIsland(IslandData islandData) {
        Player p = getPlayer(); // Obtains player

        System.out.println("Loading island data for player " + p.getName());

        data.setIslandId(islandData.getId()); // Updates Island ID in playerdata

        boolean paste = true; // variable to store pasting

        List<UUID> members = islandData.getUsers();

        System.out.println("Island users: " + members);

        // If any island member is online (island pasted)
        for (UUID uuid : members)
            if (!uuid.equals(this.uuid) && Bukkit.getPlayer(uuid) != null) {
                paste = false;
                break;
            }

        File folder = new File(main.getDataFolder() + File.separator + "cache" + File.separator + islandData.getId()); // Create cache folder

        // Pastes island if required
        if (paste) {
            SerializedFile[] islandFiles = islandData.getIslandSchematic(); // Obtains original files

            if (islandFiles == null) // Assigns default if not found
                islandFiles = SerializedFile.loadArray(main.getStartSchematic());

            SerializedFile[] files = createFiles(islandData.getId(), folder, islandFiles); // Creates cache files

            islandData.setIslandSchematic(files); // Updates schematic with cache files

            String world = main.getWorldManager().assignWorld(); // Assigns world

            System.out.println("Assigned world " + world + " for player " + p.getName());
            island = loadIsland(islandData, new WorldCreator(world).generator(main.getEmptyWorldGenerator()).createWorld()); // Loads island
        } else // If it doesn't need pasting
            island = main.getIslandManager().getIslandFromId(islandData.getId()).orElse(null); // Obtains island from ID (loaded by a teammate)

        islandData.setIsland(island); // Updates island in the island data

        SerializedLocation last = data.getLastLocation(); // Obtains last location

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
    }

    /**
     * Creates cache island files
     *
     * @param id     - The UUID (used for file name)
     * @param folder - The folder where to write the files do (default - cache)
     * @param files  - The files to put
     * @return The new renamed files
     */
    private SerializedFile[] createFiles(UUID id, File folder, SerializedFile... files) {
        SerializedFile[] copyArray = new SerializedFile[files.length];

        folder.getParentFile().mkdirs();
        folder.mkdir();

        for (int index = 0; index < files.length; index++) {
            SerializedFile file = files[index].copy();
            File realFile = null;
            try {
                realFile = file.getFile().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (realFile == null)
                continue;

            file.setFile(new File(folder, realFile.getName()));
            file.save();
            copyArray[index] = file;
        }

        return copyArray;
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
     * Pastes an island
     *
     * @param data  - The island data, used in the island object
     * @param world - The world to paste the island on
     * @return island object
     */
    private Island loadIsland(IslandData data, World world) {
        Location center = new Location(world, 256, 128, 256);
        int offset = main.getIslandConfig().getOverworldSettings().getMaxSize() >> 1;

        Location one = center.add(-offset, -128, -offset);
        Location two = center.add(offset, 128, offset);

        System.out.println("Pasting island");
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
