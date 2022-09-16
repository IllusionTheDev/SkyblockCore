package me.illusion.skyblockcore.spigot.island.impl;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandSaveEvent;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.utilities.BukkitConverter;
import me.illusion.skyblockcore.spigot.utilities.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.spigot.utilities.concurrent.MainThreadExecutor.MAIN_THREAD_EXECUTOR;

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

            saveData().join();
            Bukkit.getPluginManager().callEvent(new IslandSaveEvent(this));
        }).exceptionally(thr -> {
            ExceptionLogger.log(thr);
            return null;
        });

    }

    public Location getSpawnPoint() {
        return center.clone().add(BukkitConverter.convertLocation(data.getSpawnPointRelativeToCenter()));
    }

    public void setSpawnPoint(Location location) {
        location.setWorld(Bukkit.getWorld(world));

        Location relative = location.clone().subtract(center);

        BukkitConverter.setLocation(data.getSpawnPointRelativeToCenter(), relative);

        updateWorldBorder();
    }

    public void updateWorldBorder() {
        if (!Bukkit.isPrimaryThread()) {
            CompletableFuture.runAsync(this::updateWorldBorder, MAIN_THREAD_EXECUTOR).join(); // ensures order of execution
            // Bukkit.getScheduler().runTask(main, this::updateWorldBorder);
            return;
        }

        double range = pointOne.distance(pointTwo);

        WorldBorder border = center.getWorld().getWorldBorder();
        border.setCenter(center);
        border.setSize(range);

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

    @Override
    public void removeMember(UUID playerId) {
        data.removeUser(playerId);

        if (!data.getUsers().isEmpty())
            return;

        main.getIslandManager().unregister(this);
        main.getIslandManager().deleteIsland(this);

    }

    /**
     * Saves Island data
     */
    @Override
    public CompletableFuture<Void> saveData() {
        System.out.println("Saving island data");
        return main.getStorageHandler().save(data.getId(), data, "ISLAND");
    }

}
