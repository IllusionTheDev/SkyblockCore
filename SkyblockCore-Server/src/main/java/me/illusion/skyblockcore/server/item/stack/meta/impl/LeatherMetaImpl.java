package me.illusion.skyblockcore.server.item.stack.meta.impl;

import java.awt.Color;
import me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta;

public class LeatherMetaImpl extends ItemMetaImpl implements LeatherArmorMeta {

    private Color color;

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }
}
