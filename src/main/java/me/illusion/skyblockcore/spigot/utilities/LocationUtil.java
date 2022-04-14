package me.illusion.skyblockcore.spigot.utilities;

import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public final class LocationUtil {

    private static Method getSafeLocation;

    private LocationUtil() {
        // Empty private constructor for static Utility class
    }

    static {
        if (MinecraftVersion.getVersion().newerThan(MinecraftVersion.Version.v1_13_R1)) {
            try {
                getSafeLocation = Class.forName("me.illusion.skyblockcore.v1_13.LocationGrabber").getMethod("getSafeLocation", Location.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if a location is within a cuboid area made by 2 locations
     *
     * @param location    - The location to compare
     * @param topLeft     - The top left point of the cuboid
     * @param bottomRight - The bottom right point of the cuboid
     * @return TRUE if location is inside, FALSE otherwise
     */
    public static boolean locationBelongs(Location location, Location topLeft, Location bottomRight) {
        return !(location.getBlockY() > topLeft.getBlockY() || location.getBlockY() < bottomRight.getBlockY() || //compare y
                location.getBlockX() < topLeft.getBlockX() || location.getBlockX() > bottomRight.getBlockX() || //compare x
                location.getBlockZ() < topLeft.getBlockZ() || location.getBlockZ() > bottomRight.getBlockZ()); //compare z
    }

    public static CompletableFuture<Location> getSafeLocation(Location startLocation) {
        // Due to performance reasons, we will only check the location's own chunk;

        try {
            return (CompletableFuture<Location>) getSafeLocation.invoke(null, startLocation);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
