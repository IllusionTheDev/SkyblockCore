package me.illusion.skyblockcore.server.item.stack.meta.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.illusion.skyblockcore.server.item.stack.meta.ItemMeta;
import me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag;
import me.illusion.skyblockcore.server.item.stack.meta.impl.data.ItemMetaDataContainer;
import me.illusion.skyblockcore.server.item.stack.meta.value.BuiltinMetaValues;
import me.illusion.skyblockcore.server.item.stack.meta.value.MetaValue;

public class ItemMetaView implements ItemMeta {

    private final ItemMetaDataContainer container;

    public ItemMetaView(ItemMetaDataContainer container) {
        this.container = container;
    }

    @Override
    public <T> ItemMeta setValue(MetaValue<T> value, T object) {
        return new ItemMetaView(container.set(value, object));
    }

    @Override
    public <T> T getValue(MetaValue<T> value) {
        return container.get(value);
    }

    @Override
    public <T extends ItemMeta> T as(Class<T> clazz) {
        return ItemMetaFactory.create(clazz, container);
    }

    @Override
    public Set<ItemFlag> getFlags() {
        return new HashSet<>(getValue(BuiltinMetaValues.ITEM_FLAGS));
    }

    @Override
    public List<String> getLore() {
        return getValue(BuiltinMetaValues.LORE);
    }
}
