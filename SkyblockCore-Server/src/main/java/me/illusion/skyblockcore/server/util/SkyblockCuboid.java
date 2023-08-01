package me.illusion.skyblockcore.server.util;

public class SkyblockCuboid {

    private final double minX;
    private final double minY;
    private final double minZ;

    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public SkyblockCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);

        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public SkyblockCuboid(SkyblockLocation location1, SkyblockLocation location2) {
        this(location1.getX(), location1.getY(), location1.getZ(), location2.getX(), location2.getY(), location2.getZ());
    }

    public SkyblockCuboid(SkyblockLocation center, double radius) {
        this(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius);
    }

    public SkyblockCuboid expand(double x, double y, double z) {
        return new SkyblockCuboid(minX - x, minY - y, minZ - z, maxX + x, maxY + y, maxZ + z);
    }

    public SkyblockCuboid expand(double amount) {
        return expand(amount, amount, amount);
    }

    public SkyblockCuboid expand(SkyblockLocation location) {
        return expand(location.getX(), location.getY(), location.getZ());
    }

    public boolean contains(double x, double y, double z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    /**
     * Checks if any point in the other cuboid is contained within this cuboid.
     *
     * @param other the other cuboid
     * @return TRUE if any point in the other cuboid is contained within this cuboid, FALSE otherwise
     */
    public boolean contains(SkyblockCuboid other) {
        // the other cuboid is not guaranteed to be fully contained within this cuboid
        // none of the corners are guaranteed to be within this cuboid
        // check if any of the corners are within this cuboid
        if (contains(other.minX, other.minY, other.minZ)) {
            return true;
        }
        if (contains(other.minX, other.minY, other.maxZ)) {
            return true;
        }
        if (contains(other.minX, other.maxY, other.minZ)) {
            return true;
        }
        if (contains(other.minX, other.maxY, other.maxZ)) {
            return true;
        }
        if (contains(other.maxX, other.minY, other.minZ)) {
            return true;
        }
        if (contains(other.maxX, other.minY, other.maxZ)) {
            return true;
        }
        if (contains(other.maxX, other.maxY, other.minZ)) {
            return true;
        }
        if (contains(other.maxX, other.maxY, other.maxZ)) {
            return true;
        }

        /*
            .AA.
            BXXB
            .AA.
            intersects at X, but none of the corners are within this cuboid
         */

        return intersectsCuboid(other);
    }

    /**
     * Checks if any point in the other cuboid is contained within this cuboid.
     *
     * @param other the other cuboid
     * @return TRUE if any point in the other cuboid is contained within this cuboid, FALSE otherwise
     */
    public boolean intersectsCuboid(SkyblockCuboid other) {
        // copied from https://bukkit.org/threads/checking-if-two-cuboids-intersect.291432/, thanks to @Syd
        if (!intersectsDimension(other.getMinX(), other.getMaxX(), this.getMinX(), this.getMaxX())) {
            return false;
        }

        if (!intersectsDimension(other.getMinY(), other.getMaxY(), this.getMinY(), this.getMaxY())) {
            return false;
        }

        return intersectsDimension(other.getMinZ(), other.getMaxZ(), this.getMinZ(), this.getMaxZ());
    }

    /**
     * Checks if the two dimensions intersect.
     *
     * @param aMin the min value of the first dimension
     * @param aMax the max value of the first dimension
     * @param bMin the min value of the second dimension
     * @param bMax the max value of the second dimension
     * @return TRUE if the two dimensions intersect, FALSE otherwise
     */
    public boolean intersectsDimension(double aMin, double aMax, double bMin, double bMax) {
        return aMin <= bMax && aMax >= bMin;
    }

    public boolean contains(SkyblockLocation location) {
        return contains(location.getX(), location.getY(), location.getZ());
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public SkyblockLocation getMin() {
        return new SkyblockLocation(null, minX, minY, minZ);
    }

    public SkyblockLocation getMax() {
        return new SkyblockLocation(null, maxX, maxY, maxZ);
    }

}
