package me.illusion.utilities.storage;

import org.bukkit.Location;

public final class LocationUtil {

    private LocationUtil() {
        // Empty private constructor for static Utility class
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
}
