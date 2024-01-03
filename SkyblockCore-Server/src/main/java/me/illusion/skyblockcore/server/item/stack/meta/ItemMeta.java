package me.illusion.skyblockcore.server.item.stack.meta;

import java.util.List;
import java.util.Set;
import me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag;
import me.illusion.skyblockcore.server.item.stack.meta.value.BuiltinMetaValues;
import me.illusion.skyblockcore.server.item.stack.meta.value.MetaValue;

public interface ItemMeta {

    <T> ItemMeta setValue(MetaValue<T> value, T object);

    <T> T getValue(MetaValue<T> value);

    <T extends ItemMeta> T as(Class<T> clazz);

    default String getDisplayName() {
        return getValue(BuiltinMetaValues.DISPLAY_NAME);
    }

    default ItemMeta setDisplayName(String displayName) {
        return setValue(BuiltinMetaValues.DISPLAY_NAME, displayName);
    }

    default List<String> getLore() {
        return getValue(BuiltinMetaValues.LORE);
    }

    default ItemMeta setLore(List<String> lore) {
        return setValue(BuiltinMetaValues.LORE, lore);
    }

    default ItemMeta setLore(String... lore) {
        return setValue(BuiltinMetaValues.LORE, List.of(lore));
    }

    default Integer getCustomModelData() {
        return getValue(BuiltinMetaValues.MODEL_DATA);
    }

    default ItemMeta setCustomModelData(Integer customModelData) {
        return setValue(BuiltinMetaValues.MODEL_DATA, customModelData);
    }

    default boolean hasCustomModelData() {
        return getCustomModelData() != null;
    }

    default boolean hasFlag(ItemFlag flag) {
        return getValue(BuiltinMetaValues.ITEM_FLAGS).contains(flag);
    }

    default ItemMeta addFlag(ItemFlag flag) {
        Set<ItemFlag> flags = getValue(BuiltinMetaValues.ITEM_FLAGS);
        flags.add(flag);
        return setValue(BuiltinMetaValues.ITEM_FLAGS, flags);
    }

    default ItemMeta removeFlag(ItemFlag flag) {
        Set<ItemFlag> flags = getFlags();
        flags.remove(flag);
        return setValue(BuiltinMetaValues.ITEM_FLAGS, flags);
    }

    default Set<ItemFlag> getFlags() {
        return getValue(BuiltinMetaValues.ITEM_FLAGS);
    }

}
