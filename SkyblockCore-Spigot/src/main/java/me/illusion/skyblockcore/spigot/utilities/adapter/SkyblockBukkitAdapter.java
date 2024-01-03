package me.illusion.skyblockcore.spigot.utilities.adapter;

import java.util.concurrent.TimeUnit;
import me.illusion.cosmos.utilities.geometry.Cuboid;
import me.illusion.cosmos.utilities.time.Time;
import me.illusion.skyblockcore.common.platform.SkyblockPlatformProvider;
import me.illusion.skyblockcore.common.registry.SkyblockNamespacedKey;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;
import me.illusion.skyblockcore.server.item.MinecraftItem;
import me.illusion.skyblockcore.server.item.stack.MinecraftItemStack;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;
import me.illusion.skyblockcore.server.util.SkyblockCuboid;
import me.illusion.skyblockcore.server.util.SkyblockLocation;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.registries.BukkitMaterialRegistry;
import me.illusion.skyblockcore.spigot.registries.meta.BukkitMetaAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        return new Time(time.as(TimeUnit.SECONDS), TimeUnit.SECONDS);
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

    public static SkyblockNamespacedKey adapt(NamespacedKey key) {
        return new SkyblockNamespacedKey(key.getNamespace(), key.getKey());
    }

    public static NamespacedKey adapt(SkyblockNamespacedKey key) {
        return new NamespacedKey(key.getNamespace(), key.getKey());
    }

    public static Player adapt(SkyblockPlayer player) {
        return Bukkit.getPlayer(player.getUniqueId());
    }

    public static SkyblockPlayer adapt(Player player) {
        SkyblockServerPlatform platform = (SkyblockServerPlatform) SkyblockPlatformProvider.getPlatform();
        return platform.getPlayerManager().getPlayer(player.getUniqueId());
    }

    public static ItemStack adapt(MinecraftItemStack platformItem) {
        if (platformItem == null) {
            return null;
        }

        BukkitMaterialRegistry registry = SkyblockBukkitAdapter.getMaterialRegistry();

        MinecraftItem item = platformItem.getItem();
        Material material = registry.getMaterial(item);
        ItemMeta meta = BukkitMetaAdapter.adapt(material, platformItem.getMeta());

        ItemStack bukkitItem = new ItemStack(material, platformItem.getAmount());
        bukkitItem.setItemMeta(meta);

        return bukkitItem;
    }

    public static MinecraftItemStack adapt(ItemStack bukkitItem) {
        if (bukkitItem == null) {
            return null;
        }

        BukkitMaterialRegistry registry = SkyblockBukkitAdapter.getMaterialRegistry();

        MinecraftItem item = registry.getItem(bukkitItem.getType());
        me.illusion.skyblockcore.server.item.stack.meta.ItemMeta meta = BukkitMetaAdapter.adapt(bukkitItem.getItemMeta());

        MinecraftItemStack platformItem = MinecraftItemStack.create(item, bukkitItem.getAmount());
        platformItem.modifyMeta(m -> meta);

        return platformItem;
    }

    private static BukkitMaterialRegistry getMaterialRegistry() {
        SkyblockSpigotPlugin plugin = (SkyblockSpigotPlugin) SkyblockPlatformProvider.getPlatform();
        return plugin.getMaterialRegistry();
    }
}
