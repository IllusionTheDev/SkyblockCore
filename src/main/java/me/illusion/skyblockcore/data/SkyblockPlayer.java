package me.illusion.skyblockcore.data;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.island.Island;
import me.illusion.skyblockcore.island.IslandData;
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
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkyblockPlayer {

    private static final String SAVE_SERIALIZED = "INSERT INTO uuid_data(uuid, id) VALUES (?, ?)";
    private static final String GET_SERIALIZED = "SELECT id FROM uuid_data WHERE uuid = ?";
    private static final String LOAD_ISLAND = "SELECT id FROM island_data WHERE uuid = ?";
    private static final String SAVE_ISLAND = "INSERT INTO island_data(uuid, id) VALUES (?, ?)";

    private final CorePlugin main;

    private final UUID uuid;
    private PlayerData data;

    private Island island;

    public SkyblockPlayer(CorePlugin main, UUID uuid) {
        this.main = main;
        this.uuid = uuid;

        CompletableFuture.runAsync(() -> {
            IslandData islandData = null;
            if (!this.load()) {
                System.out.println("Inicializing Data");
                data = new PlayerData();
                System.out.println("Loading cache");

                System.out.println("Creating island data");
                islandData = new IslandData(Arrays.asList(uuid), uuid);

                System.out.println("Setting final data");
                data.setIslandSchematic(main.getStartSchematic());
                data.setMoney(0);
                data.setInventory(getPlayer().getInventory().getContents());
            }

            IslandData finalIslandData = islandData;
            Bukkit.getScheduler().runTask(main, () -> {

                System.out.println("Loading island");
                island = loadIsland(finalIslandData, main.getIslandConfig().getNetherSettings().getBukkitWorld(), data.getIslandSchematic());

                System.out.println("Running update task");

                data.setLastLocation(island.getCenter());
                checkTeleport();
                updateInventory();
                saveIsland();
            });
        });

        main.getPlayerManager().register(uuid, this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    // ----- DATA LOADING -----

    private boolean load() {
        data = (PlayerData) load(GET_SERIALIZED);

        if (data == null)
            return false;

        IslandData islandData = (IslandData) load(LOAD_ISLAND);

        getPlayer().teleport(data.getLastLocation());

        for (UUID uuid : islandData.getUsers())
            if (Bukkit.getPlayer(uuid) != null) {
                island = main.getPlayerManager().get(uuid).island;
                checkTeleport();
                return true;
            }

        World world = main.getIslandConfig().getOverworldSettings().getBukkitWorld();

        island = loadIsland(islandData, world, data.getIslandSchematic());
        checkTeleport();
        return true;
    }

    private void checkTeleport() {
        System.out.println("Teleporting");

        World world = main.getIslandConfig().getOverworldSettings().getBukkitWorld();

        String worldName = world.getName();
        String targetName = data.getLastLocation().getWorld().getName();

        if (worldName.equalsIgnoreCase(targetName))
            getPlayer().teleport(data.getLastLocation().add(island.getCenter()));
        else
            getPlayer().teleport(data.getLastLocation());
    }

    private Island loadIsland(IslandData data, World world, File schem) {
        Location one = null;
        Location two = null;
        Location center = null;
        try {
            Schematic schematic = ClipboardFormat.SCHEMATIC.load(schem);
            Clipboard clipboard = schematic.getClipboard();

            one = new Location(world, clipboard.getMinimumPoint().getBlockX(), clipboard.getMinimumPoint().getBlockY(), clipboard.getMinimumPoint().getBlockZ());
            two = new Location(world, clipboard.getMaximumPoint().getBlockX(), clipboard.getMaximumPoint().getBlockY(), clipboard.getMaximumPoint().getBlockZ());
            center = new Location(world, clipboard.getOrigin().getBlockX(), clipboard.getOrigin().getBlockY(), clipboard.getOrigin().getBlockZ());

            schematic.paste(FaweAPI.getWorld(world.getName()), clipboard.getOrigin(), false);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Island(one, two, center, data);
    }

    private Object load(String sql) {
        try {
            PreparedStatement statement = main.getMySQLConnection().prepareStatement(sql);

            statement.setString(1, uuid.toString());

            ResultSet result = statement.executeQuery();

            if(!result.first())
                return null;

            long serialized = result.getLong("id");
            return SQLSerializer.deserialize(main.getMySQLConnection(), serialized);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    // ----- DATA SAVING -----

    public void save() {
        data.setInventory(getPlayer().getInventory().getContents());
        saveIsland();
        CompletableFuture.runAsync(() -> saveObject(data, SAVE_SERIALIZED));

        if (island.getData().getUsers().stream().noneMatch(uuid -> !this.uuid.equals(uuid) && Bukkit.getPlayer(uuid) == null))
            island.cleanIsland();
    }

    private void saveObject(Object object, String SQL) {

        try {
            long id = SQLSerializer.serialize(main.getMySQLConnection(), object);
            PreparedStatement statement = main.getMySQLConnection().prepareStatement(SQL);

            statement.setString(1, uuid.toString());
            statement.setLong(2, id);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----- DATA POST-LOAD -----

    private void updateInventory() {
        if (data == null)
            getPlayer().getInventory().clear();

        getPlayer().getInventory().setContents(data.getInventory());
    }

    // ----- DATA PRE-SAVE -----

    public void saveIsland() {
        CuboidRegion region = new CuboidRegion(FaweAPI.getWorld(island.getCenter().getWorld().getName()),
                new Vector(island.getPointOne().getBlockX(), island.getPointOne().getBlockY(), island.getPointOne().getBlockZ()),
                new Vector(island.getPointTwo().getBlockX(), island.getPointTwo().getBlockY(), island.getPointTwo().getBlockZ()));

        Schematic schematic = new Schematic(region);
        Clipboard board = schematic.getClipboard();

        board.setOrigin(new Vector(island.getCenter().getBlockX(), island.getCenter().getBlockY(), island.getCenter().getBlockZ()));

        try {
            schematic.save(data.getIslandSchematic(), ClipboardFormats.findByFile(data.getIslandSchematic()));

            CompletableFuture.runAsync(() -> saveObject(island.getData(), SAVE_ISLAND));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
