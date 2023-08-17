package me.illusion.skyblockcore.spigot.utilities.adapter;

import java.util.concurrent.TimeUnit;
import me.illusion.cosmos.utilities.geometry.Cuboid;
import me.illusion.cosmos.utilities.time.Time;
import me.illusion.skyblockcore.server.util.SkyblockCuboid;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Bukkit adapter for SkyblockCore.
 */
public final class SkyblockBukkitAdapter {

    private SkyblockBukkitAdapter() {
    }

    /**
     * Converts a {@link SkyblockLocation} to a {@link Location}.
     *
     * @param location The location to convert.
     * @return The converted location.
     */
    public static Location toBukkitLocation(SkyblockLocation location) {
        return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
    }

    /**
     * Converts a {@link Location} to a {@link SkyblockLocation}.
     *
     * @param location The location to convert.
     * @return The converted location.
     */
    public static SkyblockLocation toSkyblockLocation(Location location) {
        return new SkyblockLocation(location.getWorld() == null ? null : location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    /**
     * Converts a {@link Vector} to a {@link SkyblockLocation}.
     *
     * @param vector The vector to convert.
     * @return The converted location.
     */
    public static SkyblockLocation toSkyblockLocation(Vector vector) {
        return new SkyblockLocation(null, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Converts a Skyblock common {@link Time} to a Cosmos {@link Time}.
     *
     * @param time The time to convert.
     * @return The converted time.
     */
    public static Time asCosmosTime(me.illusion.skyblockcore.common.utilities.time.Time time) {
        return new Time((int) time.as(TimeUnit.SECONDS), TimeUnit.SECONDS);
    }

    /**
     * Converts a Cosmos {@link Time} to a Skyblock common {@link Time}.
     *
     * @param time The time to convert.
     * @return The converted time.
     */
    public static me.illusion.skyblockcore.common.utilities.time.Time asSkyblockTime(Time time) {
        return new me.illusion.skyblockcore.common.utilities.time.Time((int) time.as(TimeUnit.SECONDS), TimeUnit.SECONDS);
    }

    /**
     * Converts a Cosmos {@link Cuboid} to a Skyblock server {@link SkyblockCuboid}.
     *
     * @param cuboid The cuboid to convert.
     * @return The converted cuboid.
     */
    public static SkyblockCuboid toSkyblockCuboid(Cuboid cuboid) {
        return new SkyblockCuboid(toSkyblockLocation(cuboid.getMin()), toSkyblockLocation(cuboid.getMax()));
    }

    /**
     * Converts a Skyblock server {@link SkyblockCuboid} to a Cosmos {@link Cuboid}.
     *
     * @param cuboid The cuboid to convert.
     * @return The converted cuboid.
     */
    public static Cuboid toCosmosCuboid(SkyblockCuboid cuboid) {
        return new Cuboid(toBukkitLocation(cuboid.getMin()), toBukkitLocation(cuboid.getMax()));
    }

}
