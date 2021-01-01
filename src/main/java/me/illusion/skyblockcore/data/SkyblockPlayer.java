package me.illusion.skyblockcore.data;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.google.common.io.Files;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.island.Island;
import me.illusion.skyblockcore.island.IslandData;
import me.illusion.skyblockcore.island.grid.GridCell;
import me.illusion.skyblockcore.sql.SQLSerializer;
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
        File schematic = new File(main.getDataFolder() + File.separator + "cache", uuid.toString() + ".schematic");
        IslandData islandData;

        try {
            schematic.getParentFile().mkdirs();
            schematic.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data == null) {
            data = new PlayerData();
            Files.copy(main.getStartSchematic(), schematic);
            islandData = new IslandData(UUID.randomUUID(), schematic, uuid.toString(), uuid, new ArrayList<>(), null);
            data.getInventory().updateArray(getPlayer().getInventory().getContents());

        } else {
            islandData = (IslandData) load(GET_ISLAND, "ISLAND", data.getIslandId().toString());
            Files.copy(islandData.getIslandSchematic(), schematic);
        }

        data.setIslandId(islandData.getId());

        Bukkit.getScheduler().runTask(main, () -> {
            island = loadIsland(islandData, main.getIslandConfig().getOverworldSettings().getBukkitWorld());
            islandData.setIsland(island);

            if (data.getLastLocation().getLocation() == null) {
                data.getLastLocation().update(getPlayer().getLocation());
                data.getIslandLocation().update(getPlayer().getLocation());
            }

            checkTeleport();
            updateInventory();
        });
    }

    /**
     * Teleports the player to its island relative position
     * If the location isn't on the island world, the location is no longer relative
     */
    private void checkTeleport() {
        System.out.println("Teleporting");

        World world = main.getIslandConfig().getOverworldSettings().getBukkitWorld();

        String worldName = world.getName();
        String schematicName = data.getLastLocation().getLocation().getWorld().getName();

        if (worldName.equalsIgnoreCase(schematicName))
            getPlayer().teleport(data.getLastLocation().getLocation().add(island.getCenter().getX(), 0, island.getCenter().getZ()));
        else
            getPlayer().teleport(data.getLastLocation().getLocation());
    }

    /**
     * Pastes an island
     *
     * @param data  - The island data, used in the island object
     * @param world - The world to paste the island on
     * @return island object
     */
    private Island loadIsland(IslandData data, World world) {
        Location one = null;
        Location two = null;
        Location center = null;
        GridCell cell = main.getGrid().getFirstCell();

        cell.setOccupied(true);

        try {
            Schematic schematic = ClipboardFormat.SCHEMATIC.load(data.getIslandSchematic());
            Clipboard clipboard = schematic.getClipboard();

            one = new Location(world, clipboard.getMinimumPoint().getBlockX(), clipboard.getMinimumPoint().getBlockY(), clipboard.getMinimumPoint().getBlockZ());
            two = new Location(world, clipboard.getMaximumPoint().getBlockX(), clipboard.getMaximumPoint().getBlockY(), clipboard.getMaximumPoint().getBlockZ());
            center = new Location(world, clipboard.getOrigin().getBlockX(), clipboard.getOrigin().getBlockY(), clipboard.getOrigin().getBlockZ());

            int distance = main.getIslandConfig().getOverworldSettings().getDistance();
            center = center.add(cell.getXPos() * distance, 0, cell.getYPos() * distance);
            one = one.add(cell.getXPos() * distance, 0, cell.getYPos() * distance);
            two = two.add(cell.getXPos() * distance, 0, cell.getYPos() * distance);

            Bukkit.getLogger().info(String.format("Pasting island at %s %s %s (%s)", center.getX(), center.getY(), center.getZ(), center.getWorld().getName()));
            schematic.paste(FaweAPI.getWorld(world.getName()), clipboard.getOrigin(), false);


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Pasted Island.");

        return new Island(main, one, two, center, data, cell);
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
        } catch (SQLException | IOException | ClassNotFoundException e) {
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
        data.getLastLocation().update(getPlayer().getLocation());
        data.getIslandLocation().update(getPlayer().getLocation());
        data.getInventory().updateArray(getPlayer().getInventory().getContents());
        island.save();

        CompletableFuture.runAsync(() -> saveObject(data, SAVE_PLAYER));

        boolean delete = true;

        for (UUID uuid : island.getData().getUsers()) {
            if (uuid.equals(this.uuid))
                continue;
            if (Bukkit.getPlayer(uuid) == null)
                continue;
            delete = false;
        }

        if (delete)
            island.cleanIsland();

        island.getData().getIslandSchematic().delete();
    }

    /**
     * Serializes the object and sets the serialized ID into the SQL statement
     *
     * @param object - The object to serialize
     * @param SQL    - The SQL query
     */
    private void saveObject(Object object, String SQL) {

        try {
            long id = SQLSerializer.serialize(main.getMySQLConnection(), object, "PLAYER");
            PreparedStatement statement = main.getMySQLConnection().prepareStatement(SQL);

            statement.setString(1, uuid.toString());
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
