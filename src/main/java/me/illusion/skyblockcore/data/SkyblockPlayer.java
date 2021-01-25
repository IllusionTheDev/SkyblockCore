package me.illusion.skyblockcore.data;

import com.google.common.io.Files;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.island.Island;
import me.illusion.skyblockcore.island.IslandData;
import me.illusion.skyblockcore.sql.SQLSerializer;
import me.illusion.skyblockcore.sql.serialized.SerializedLocation;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.sql.SQLOperation.*;

@Getter
public class SkyblockPlayer {

    @Getter(AccessLevel.NONE)
    private final CorePlugin main;

    private final UUID uuid;
    private PlayerData data;

    private Location islandCenter;
    private Island island;

    public SkyblockPlayer(CorePlugin main, UUID uuid) {
        this.main = main;
        this.uuid = uuid;

        CompletableFuture.runAsync(this::load);

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

        data = (PlayerData) load(GET_PLAYER, "PLAYER", uuid.toString());
        IslandData islandData;

        File folder = new File(main.getDataFolder() + File.separator + "cache");

        if (data == null) {
            data = new PlayerData();
            islandData = new IslandData(UUID.randomUUID(), uuid, new ArrayList<>());
            data.getInventory().updateArray(getPlayer().getInventory().getContents());
        } else {
            islandData = (IslandData) load(GET_ISLAND, "ISLAND", data.getIslandId().toString());
        }

        UUID uuid = islandData.getId();
        File[] islandFiles = islandData.getIslandSchematic();

        if (islandFiles == null)
            islandFiles = main.getStartSchematic();

        File[] files = createFiles(uuid, folder, islandFiles);

        islandData.setIslandSchematic(files);

        data.setIslandId(uuid);


        Bukkit.getScheduler().runTask(main, () -> {
            String world = main.getWorldManager().assignWorld(uuid);

            island = loadIsland(islandData, Bukkit.getWorld(world));
            islandData.setIsland(island);

            SerializedLocation last = data.getLastLocation();

            if (last.getLocation() == null) {
                last.update(getPlayer().getLocation());
                data.getIslandLocation().update(getPlayer().getLocation());
            }

            checkTeleport();
            updateInventory();
        });
    }

    private File[] createFiles(UUID id, File folder, File... files) {
        File[] copyArray = new File[files.length];

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            File copy = new File(folder, id + "_" + i + FilenameUtils.getExtension(file.getName()));

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
     * @param sql - The SQL query used to obtain the ID
     * @return deserialized object
     */
    @SneakyThrows
    private Object load(String sql, String table, String... values) {
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = main.getMySQLConnection().prepareStatement(sql);
            for (int i = 1; i <= values.length; i++)
                statement.setString(i, values[i - 1]);

            result = statement.executeQuery();

            if (!result.first())
                return null;

            long serialized = result.getLong("id");
            return SQLSerializer.deserialize(main.getMySQLConnection(), serialized, table);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return null;
    }
    // ----- DATA SAVING -----

    /**
     * Saves all the player data, and cleans the island if possible
     */
    public void save() {
        Player p = getPlayer();
        Location loc = p.getLocation();

        data.getLastLocation().update(loc);
        data.getIslandLocation().update(loc);
        data.getInventory().updateArray(p.getInventory().getContents());
        island.save();

        CompletableFuture.runAsync(() -> saveObject(data, SAVE_PLAYER));

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
            file.delete();
    }

    /**
     * Serializes the object and sets the serialized ID into the SQL statement
     *
     * @param object - The object to serialize
     * @param SQL    - The SQL query
     */
    @SneakyThrows
    private void saveObject(Object object, String SQL) {

        PreparedStatement statement = null;
        try {
            long id = SQLSerializer.serialize(main.getMySQLConnection(), object, "PLAYER");
            statement = main.getMySQLConnection().prepareStatement(SQL);

            statement.setString(1, uuid.toString());
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
        }
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

}
