package me.illusion.skyblockcore.spigot.utilities.adapter;

import java.util.concurrent.TimeUnit;
import me.illusion.cosmos.utilities.geometry.Cuboid;
import me.illusion.cosmos.utilities.time.Time;
import me.illusion.skyblockcore.server.util.SkyblockCuboid;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public final class SkyblockBukkitAdapter {

    public static Location toBukkitLocation(SkyblockLocation location) {
        return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
    }

    public static SkyblockLocation toSkyblockLocation(Location location) {
        return new SkyblockLocation(location.getWorld() == null ? null : location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static SkyblockLocation toSkyblockLocation(Vector vector) {
        return new SkyblockLocation(null, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Time asCosmosTime(me.illusion.skyblockcore.common.utilities.time.Time time) {
        return new Time((int) time.as(TimeUnit.SECONDS), TimeUnit.SECONDS);
    }

    public static me.illusion.skyblockcore.common.utilities.time.Time asSkyblockTime(Time time) {
        return new me.illusion.skyblockcore.common.utilities.time.Time((int) time.as(TimeUnit.SECONDS), TimeUnit.SECONDS);
    }

    public static SkyblockCuboid toSkyblockCuboid(Cuboid cuboid) {
        return new SkyblockCuboid(toSkyblockLocation(cuboid.getMin()), toSkyblockLocation(cuboid.getMax()));
    }

    public static Cuboid toCosmosCuboid(SkyblockCuboid cuboid) {
        return new Cuboid(toBukkitLocation(cuboid.getMin()), toBukkitLocation(cuboid.getMax()));
    }

}
