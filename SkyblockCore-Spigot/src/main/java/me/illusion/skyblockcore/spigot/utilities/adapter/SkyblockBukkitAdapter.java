package me.illusion.skyblockcore.spigot.utilities.adapter;

import java.util.concurrent.TimeUnit;
import me.illusion.cosmos.utilities.time.Time;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class SkyblockBukkitAdapter {

    public static Location toBukkitLocation(SkyblockLocation location) {
        return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
    }

    public static SkyblockLocation toSkyblockLocation(Location location) {
        return new SkyblockLocation(location.getWorld() == null ? null : location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static Time asCosmosTime(me.illusion.skyblockcore.common.utilities.time.Time time) {
        return new Time((int) time.as(TimeUnit.SECONDS), TimeUnit.SECONDS);
    }

    public static me.illusion.skyblockcore.common.utilities.time.Time asSkyblockTime(Time time) {
        return new me.illusion.skyblockcore.common.utilities.time.Time((int) time.as(TimeUnit.SECONDS), TimeUnit.SECONDS);
    }

}
