package me.illusion.skyblockcore.server.item;

import me.illusion.skyblockcore.common.registry.Keyed;
import me.illusion.skyblockcore.common.registry.SkyblockNamespacedKey;

public abstract class MinecraftItem implements Keyed {

    private final SkyblockNamespacedKey key;

    protected MinecraftItem(SkyblockNamespacedKey key) {
        this.key = key;
    }

    public abstract MinecraftMaterial attemptCreateMaterial();

    @Override
    public SkyblockNamespacedKey getKey() {
        return key;
    }
}
