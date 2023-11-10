package me.illusion.skyblockcore.spigot.registries.meta.converter;

import org.bukkit.inventory.meta.ItemMeta;

public interface MetaConverter<K extends ItemMeta, V extends me.illusion.skyblockcore.server.item.stack.ItemMeta> {

    Class<K> getMetaClass();

    Class<V> getSkyblockMetaClass();

    V convert(K bukkitMeta);

}
