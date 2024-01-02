package me.illusion.skyblockcore.server.item;

import me.illusion.skyblockcore.common.registry.Keyed;
import me.illusion.skyblockcore.common.registry.SkyblockNamespacedKey;
import me.illusion.skyblockcore.server.item.stack.meta.ItemMeta;

public interface MinecraftItem extends Keyed {

    MinecraftMaterial getMaterial();

    int getMaxStackSize();

    ItemMeta createItemMeta();

    @Override
    default SkyblockNamespacedKey getKey() {
        return getMaterial().getKey();
    }
}
