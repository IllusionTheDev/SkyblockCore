package me.illusion.skyblockcore.spigot.island.impl;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandSaveEvent;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.utilities.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@Getter
public class LoadedIsland implements Island {

    private final SkyblockPlugin main;

    private final Location pointOne;
    private final Location pointTwo;
    private final Location center;

    private final IslandData data;

    private final String world;

    public LoadedIsland(SkyblockPlugin main, Location pointOne, Location pointTwo, Location center, IslandData data, String world) {
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
    @Override
    public CompletableFuture<Void> save() {
        return main.getIslandDependencies().getPastingHandler().save(this, schem -> {
            data.setIslandSchematic(schem);

            Bukkit.getPluginManager().callEvent(new IslandSaveEvent(this));
        });

    }

    public Location getSpawnPoint() {
        return center.add(data.getSpawnPointRelativeToCenter().getLocation());
    }

    public void setSpawnPoint(Location location) {
        Location relative = location.subtract(center);

        data.getSpawnPointRelativeToCenter().update(relative, world);
    }

    public void teleport(Player player) {
        player.teleport(getSpawnPoint());
    }

    @Override
    public String getWorldName() {
        return world;
    }

    @Override
    public boolean locationBelongs(Location location) {
        return LocationUtil.locationBelongs(location, pointOne, pointTwo);
    }

    /**
     * Saves Island data
     */
    @Override
    public CompletableFuture<Void> saveData() {
        return main.getStorageHandler().save(data.getId(), data, "ISLAND");
    }

}
