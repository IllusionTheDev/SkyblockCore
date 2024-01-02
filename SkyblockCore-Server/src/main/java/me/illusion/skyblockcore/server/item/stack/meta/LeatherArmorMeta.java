package me.illusion.skyblockcore.server.item.stack.meta;

import java.awt.Color;
import me.illusion.skyblockcore.server.item.stack.meta.value.BuiltinMetaValues;

public interface LeatherArmorMeta extends ItemMeta {

    default Color getColor() {
        return getValue(BuiltinMetaValues.LEATHER_COLOR);
    }

    default ItemMeta setColor(Color color) {
        return setValue(BuiltinMetaValues.LEATHER_COLOR, color);
    }

}
