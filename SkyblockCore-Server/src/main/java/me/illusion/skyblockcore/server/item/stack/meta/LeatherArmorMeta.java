package me.illusion.skyblockcore.server.item.stack.meta;

import java.awt.Color;
import me.illusion.skyblockcore.server.item.stack.ItemMeta;

public interface LeatherArmorMeta extends ItemMeta {

    Color getColor();
    void setColor(Color color);

}
