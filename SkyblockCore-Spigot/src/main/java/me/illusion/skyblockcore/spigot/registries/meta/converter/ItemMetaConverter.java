package me.illusion.skyblockcore.spigot.registries.meta.converter;

import org.bukkit.inventory.meta.ItemMeta;

public abstract class ItemMetaConverter {

    protected void setData(ItemMeta bukkitMeta, me.illusion.skyblockcore.server.item.stack.ItemMeta skyblockMeta) {
        skyblockMeta.setLore(bukkitMeta.getLore());
        skyblockMeta.setDisplayName(bukkitMeta.getDisplayName());
        skyblockMeta.setCustomModelData(bukkitMeta.getCustomModelData());
    }
}
