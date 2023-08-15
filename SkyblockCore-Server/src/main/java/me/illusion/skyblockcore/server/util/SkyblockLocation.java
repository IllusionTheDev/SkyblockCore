package me.illusion.skyblockcore.server.util;

/**
 * Represents a multi-platform location. It is expected that each platform makes an adapter for this class.
 */
public class SkyblockLocation {

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public SkyblockLocation(String world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public SkyblockLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
