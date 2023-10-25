package me.illusion.skyblockcore.server.item;

import me.illusion.skyblockcore.server.item.stack.ItemMeta;
import me.illusion.skyblockcore.common.registry.Keyed;
import me.illusion.skyblockcore.common.registry.SkyblockNamespacedKey;

public interface MinecraftMaterial extends Keyed {

    MinecraftItem getItem();

    int getMaxStackSize();

    ItemMeta createItemMeta();

    @Override
    default SkyblockNamespacedKey getKey() {
        return getItem().getKey();
    }
}
