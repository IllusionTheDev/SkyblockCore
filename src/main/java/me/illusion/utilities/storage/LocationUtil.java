package me.illusion.utilities.storage;

import org.bukkit.Location;

public final class LocationUtil {

    private LocationUtil() {
        // Empty private constructor for static Utility class
    }

    public static boolean locationBelongs(Location location, Location topLeft, Location bottomRight) {
        return !(location.getBlockY() > topLeft.getBlockY() || location.getBlockY() < bottomRight.getBlockY() || //compare y
                location.getBlockX() < topLeft.getBlockX() || location.getBlockX() > bottomRight.getBlockX() || //compare x
                location.getBlockZ() < topLeft.getBlockZ() || location.getBlockZ() > bottomRight.getBlockZ()); //compare z
    }
}
