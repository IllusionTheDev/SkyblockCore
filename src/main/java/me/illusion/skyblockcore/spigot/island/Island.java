package me.illusion.skyblockcore.spigot.island;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.sql.SQLSerializer;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

@Getter
public class Island {

    private final SkyblockPlugin main;

    private final Location pointOne;
    private final Location pointTwo;
    private final Location center;

    private final IslandData data;

    private final String world;

    public Island(SkyblockPlugin main, Location pointOne, Location pointTwo, Location center, IslandData data, String world) {
        this.main = main;
        this.pointOne = pointOne;
        this.pointTwo = pointTwo;
        this.center = center;
        this.data = data;
        this.world = world;

        main.getIslandManager().register(this);
    }

    /**
     * Saves the island
     */
    public void save(Runnable afterSave) {
        main.getPastingHandler().save(this, schem -> {
            data.setIslandSchematic(schem);

            CompletableFuture.runAsync(this::saveData);
            afterSave.run();
        });
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

        main.getIslandManager().unregister(this);
        main.getWorldManager().unregister(this.world);
    }

    /**
     * Saves Island data
     */
    private void saveData() {
        SQLSerializer.serialize(main.getMySQLConnection(), data.getId(), data, "ISLAND");
    }

}
