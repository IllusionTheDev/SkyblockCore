package me.illusion.skyblockcore.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.sql.SQLSerializer;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.concurrent.CompletableFuture;

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

        CompletableFuture.runAsync(this::saveData);
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

        world.save();

        main.getWorldManager().unregister(this.world);
    }

    /**
     * Saves Island data
     */
    private void saveData() {
        SQLSerializer.serialize(main.getMySQLConnection(), data.getId(), data, "ISLAND");
    }

}
