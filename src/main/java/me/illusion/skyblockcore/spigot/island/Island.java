package me.illusion.skyblockcore.spigot.island;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
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

            CompletableFuture.runAsync(this::saveData)
                    .thenRun(afterSave);
        });
    }

    /**
     * Cleans the island, by regenerating its chunks
     */
    public void cleanIsland() {
        World world = center.getWorld();

        Bukkit.unloadWorld(world, true);

        main.getIslandManager().unregister(this);
        main.getWorldManager().unregister(this.world);

        main.getWorldManager().whenNextUnload(unloadedWorld -> {
            File regionFolder = new File(world.getWorldFolder() + File.separator + "region");
            regionFolder.delete();
            regionFolder.mkdir();
        }, this.world);
    }

    /**
     * Saves Island data
     */
    private void saveData() {
        main.getStorageHandler().save(data.getId(), data, "ISLAND");
    }

}
