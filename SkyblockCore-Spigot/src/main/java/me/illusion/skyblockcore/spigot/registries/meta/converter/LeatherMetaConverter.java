package me.illusion.skyblockcore.spigot.registries.meta.converter;

import me.illusion.skyblockcore.server.item.stack.meta.impl.LeatherMetaImpl;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherMetaConverter extends ItemMetaConverter implements
    MetaConverter<LeatherArmorMeta, me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta> {

    @Override
    public Class<LeatherArmorMeta> getMetaClass() {
        return LeatherArmorMeta.class;
    }

    @Override
    public Class<me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta> getSkyblockMetaClass() {
        return null;
    }

    @Override
    public me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta convert(LeatherArmorMeta bukkitMeta) {
        LeatherMetaImpl impl = new LeatherMetaImpl();
        setData(bukkitMeta, impl);
        return impl;
    }

    @Override
    protected void setData(ItemMeta bukkitMeta, me.illusion.skyblockcore.server.item.stack.ItemMeta skyblockMeta) {
        super.setData(bukkitMeta, skyblockMeta);

        LeatherArmorMeta impl = (LeatherArmorMeta) bukkitMeta;

        Color bukkitColor = impl.getColor();
        java.awt.Color awtColor = new java.awt.Color(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue());

        ((me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta) skyblockMeta).setColor(awtColor);
    }
}
