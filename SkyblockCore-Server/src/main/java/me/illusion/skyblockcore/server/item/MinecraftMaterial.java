package me.illusion.skyblockcore.server.item;

import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.platform.SkyblockPlatformProvider;
import me.illusion.skyblockcore.common.registry.Keyed;
import me.illusion.skyblockcore.common.registry.Registry;
import me.illusion.skyblockcore.common.registry.SkyblockNamespacedKey;

public abstract class MinecraftMaterial implements Keyed {

    private final SkyblockNamespacedKey key;

    protected MinecraftMaterial(SkyblockNamespacedKey key) {
        this.key = key;
    }

    public static MinecraftMaterial of(SkyblockNamespacedKey key) {
        SkyblockPlatform platform = SkyblockPlatformProvider.getPlatform();
        Registry<MinecraftMaterial> registry = platform.getRegistries().getRegistry(MinecraftMaterial.class);
        return registry.get(key);
    }

    public static MinecraftMaterial of(String key) {
        return MinecraftMaterial.of(SkyblockNamespacedKey.minecraft(key));
    }

    public abstract MinecraftItem attemptCreateItem();

    @Override
    public SkyblockNamespacedKey getKey() {
        return key;
    }
}
