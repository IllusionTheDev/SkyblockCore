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

    public SkyblockLocation add(double x, double y, double z) {
        return new SkyblockLocation(world, this.x + x, this.y + y, this.z + z, yaw, pitch);
    }

    public SkyblockLocation subtract(double x, double y, double z) {
        return new SkyblockLocation(world, this.x - x, this.y - y, this.z - z, yaw, pitch);
    }

    public SkyblockLocation multiply(double x, double y, double z) {
        return new SkyblockLocation(world, this.x * x, this.y * y, this.z * z, yaw, pitch);
    }

    public SkyblockLocation divide(double x, double y, double z) {
        return new SkyblockLocation(world, this.x / x, this.y / y, this.z / z, yaw, pitch);
    }

    public SkyblockLocation add(SkyblockLocation location) {
        return add(location.x, location.y, location.z);
    }

    public SkyblockLocation subtract(SkyblockLocation location) {
        return subtract(location.x, location.y, location.z);
    }

    public SkyblockLocation multiply(SkyblockLocation location) {
        return multiply(location.x, location.y, location.z);
    }

    public SkyblockLocation divide(SkyblockLocation location) {
        return divide(location.x, location.y, location.z);
    }

    public SkyblockLocation set(double x, double y, double z) {
        return new SkyblockLocation(world, x, y, z, yaw, pitch);
    }

    public SkyblockLocation setYaw(float yaw) {
        return new SkyblockLocation(world, x, y, z, yaw, pitch);
    }

    public SkyblockLocation setPitch(float pitch) {
        return new SkyblockLocation(world, x, y, z, yaw, pitch);
    }

    public SkyblockLocation setX(double x) {
        return new SkyblockLocation(world, x, y, z, yaw, pitch);
    }

    public SkyblockLocation setY(double y) {
        return new SkyblockLocation(world, x, y, z, yaw, pitch);
    }

    public SkyblockLocation setZ(double z) {
        return new SkyblockLocation(world, x, y, z, yaw, pitch);
    }

}
