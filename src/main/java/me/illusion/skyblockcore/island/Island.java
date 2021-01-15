package me.illusion.skyblockcore.island;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.island.grid.GridCell;
import me.illusion.skyblockcore.sql.SQLSerializer;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.sql.SQLOperation.SAVE_ISLAND;

@Getter
@AllArgsConstructor
public class Island {

    private final CorePlugin main;

    private final Location pointOne;
    private final Location pointTwo;
    private final Location center;

    private final IslandData data;

    private final GridCell cell;

    /**
     * Saves the island
     */
    public void save() {
        CuboidRegion region = new CuboidRegion(FaweAPI.getWorld(center.getWorld().getName()),
                new Vector(pointOne.getBlockX(), pointOne.getBlockY(), pointOne.getBlockZ()),
                new Vector(pointTwo.getBlockX(), pointTwo.getBlockY(), pointTwo.getBlockZ()));

        Schematic schematic = new Schematic(region);
        Clipboard board = schematic.getClipboard();

        board.setOrigin(new Vector(center.getBlockX(), center.getBlockY(), center.getBlockZ()));

        try {
            File schem = data.getIslandSchematic();
            schematic.save(schem, ClipboardFormats.findByFile(schem));

            CompletableFuture.runAsync(() -> saveObject(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cleans the island, by regenerating its chunks
     */
    public void cleanIsland() {
        World world = center.getWorld();

        CuboidRegion region = new CuboidRegion(FaweAPI.getWorld(world.getName()),
                new Vector(pointOne.getBlockX(), pointOne.getBlockY(), pointOne.getBlockZ()),
                new Vector(pointTwo.getBlockX(), pointTwo.getBlockY(), pointTwo.getBlockZ()));

        for (Vector2D chunk : region.getChunks())
            world.regenerateChunk(chunk.getBlockX(), chunk.getBlockZ());
    }

    /**
     * Serializes the object and sets the serialized ID into the SQL statement
     *
     * @param object - The object to serialize
     */
    @SneakyThrows
    private void saveObject(Object object) {

        PreparedStatement statement = null;
        try {
            long id = SQLSerializer.serialize(main.getMySQLConnection(), object, "ISLAND");
            statement = main.getMySQLConnection().prepareStatement(SAVE_ISLAND);

            statement.setString(1, data.getId().toString());
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
        }
    }

}
