package me.illusion.skyblockcore.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.sql.SQLSerializer;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
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

    private final String world;

    /**
     * Saves the island
     */
    public void save() {
        File[] schem = main.getPastingHandler().save(this);
        data.setIslandSchematic(schem);

        CompletableFuture.runAsync(() -> saveObject(data));
    }

    /**
     * Cleans the island, by regenerating its chunks
     */
    public void cleanIsland() {
        World world = center.getWorld();

        int x1 = pointOne.getBlockX() >> 4;
        int z1 = pointOne.getBlockZ() >> 4;
        int x2 = pointTwo.getBlockX() >> 4;
        int z2 = pointTwo.getBlockZ() >> 4;

        for (int x = x1; x <= x2; x++)
            for (int z = z1; z <= z2; z++)
                world.regenerateChunk(x, z);

        main.getWorldManager().unregister(this.world);
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
