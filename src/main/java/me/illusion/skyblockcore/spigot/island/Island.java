package me.illusion.skyblockcore.spigot.island;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandSaveEvent;
import me.illusion.skyblockcore.spigot.utilities.WorldUtils;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

        if (data.getSpawnPointRelativeToCenter() == null) {
            data.setSpawnPointRelativeToCenter(new SerializedLocation());
            setSpawnPoint(center);
        }

        main.getIslandManager().register(this);
    }

    /**
     * Saves the island
     */
    public void save(Runnable afterSave) {
        main.getPastingHandler().save(this, schem -> {
            data.setIslandSchematic(schem);

            Bukkit.getPluginManager().callEvent(new IslandSaveEvent(this));
            saveData().thenRun(afterSave);
        });
    }

    /**
     * Cleans the island, by regenerating its chunks
     */
    public void cleanIsland() {
        System.out.println("Cleaning island...");

        WorldUtils
                .unload(main, world)
                .thenRun(() -> {
                    WorldUtils.deleteRegionFolder(main, world);
                    main.getIslandManager().unregister(this);

                    new ScheduleBuilder(main) // Intentional delay so we don't corrupt worlds by loading and unloading very fast
                            .in(main.getSettings().getReleaseDelay()).ticks()
                            .run(() -> main.getWorldManager().unregister(this.world))
                            .sync()
                            .start();

                });


    }

    public Location getSpawnPoint() {
        return center.add(data.getSpawnPointRelativeToCenter().getLocation());
    }

    public void setSpawnPoint(Location location) {
        Location relative = location.subtract(center);

        data.getSpawnPointRelativeToCenter().update(relative);
    }

    public void teleport(Player player) {
        player.teleport(getSpawnPoint());
    }

    /**
     * Saves Island data
     */
    private CompletableFuture<Void> saveData() {
        return main.getStorageHandler().save(data.getId(), data, "ISLAND");
    }

}
