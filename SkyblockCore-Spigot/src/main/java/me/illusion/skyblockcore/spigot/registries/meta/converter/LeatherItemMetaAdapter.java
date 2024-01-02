package me.illusion.skyblockcore.spigot.registries.meta.converter;

import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherItemMetaAdapter implements MetaAdapter<LeatherArmorMeta, me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta> {

    @Override
    public Class<LeatherArmorMeta> getBukkitMetaClass() {
        return LeatherArmorMeta.class;
    }

    @Override
    public Class<me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta> getPlatformMetaClass() {
        return me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta.class;
    }

    @Override
    public void convertToPlatform(LeatherArmorMeta bukkitMeta, me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta platformMeta) {
        Color bukkitColor = bukkitMeta.getColor();
        java.awt.Color platformColor = new java.awt.Color(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue());

        platformMeta.setColor(platformColor);
    }

    @Override
    public void convertToBukkit(me.illusion.skyblockcore.server.item.stack.meta.LeatherArmorMeta platformMeta, LeatherArmorMeta bukkitMeta) {
        java.awt.Color platformColor = platformMeta.getColor();
        Color bukkitColor = Color.fromRGB(platformColor.getRed(), platformColor.getGreen(), platformColor.getBlue());

        bukkitMeta.setColor(bukkitColor);
    }
}
