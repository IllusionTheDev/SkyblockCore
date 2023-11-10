package me.illusion.skyblockcore.spigot.registries.meta.converter;

import me.illusion.skyblockcore.server.item.stack.meta.impl.ItemMetaImpl;
import org.bukkit.inventory.meta.ItemMeta;

public class SimpleMetaConverter extends ItemMetaConverter implements MetaConverter<ItemMeta, me.illusion.skyblockcore.server.item.stack.ItemMeta> {

    @Override
    public Class<ItemMeta> getMetaClass() {
        return ItemMeta.class;
    }

    @Override
    public Class<me.illusion.skyblockcore.server.item.stack.ItemMeta> getSkyblockMetaClass() {
        return me.illusion.skyblockcore.server.item.stack.ItemMeta.class;
    }

    @Override
    public me.illusion.skyblockcore.server.item.stack.ItemMeta convert(ItemMeta bukkitMeta) {
        ItemMetaImpl impl = new ItemMetaImpl();
        setData(bukkitMeta, impl);
        return impl;
    }
}
