package me.illusion.skyblockcore.spigot.registries.meta.converter;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class DefaultItemMetaAdapter implements MetaAdapter<ItemMeta, me.illusion.skyblockcore.server.item.stack.meta.ItemMeta> {

    @Override
    public Class<ItemMeta> getBukkitMetaClass() {
        return ItemMeta.class;
    }

    @Override
    public Class<me.illusion.skyblockcore.server.item.stack.meta.ItemMeta> getPlatformMetaClass() {
        return me.illusion.skyblockcore.server.item.stack.meta.ItemMeta.class;
    }

    @Override
    public void convertToPlatform(ItemMeta bukkitMeta, me.illusion.skyblockcore.server.item.stack.meta.ItemMeta platformMeta) {
        platformMeta.setLore(bukkitMeta.getLore());
        platformMeta.setDisplayName(bukkitMeta.getDisplayName());
        platformMeta.setCustomModelData(bukkitMeta.getCustomModelData());

        for (ItemFlag bukkitFlag : bukkitMeta.getItemFlags()) {
            platformMeta.addFlag(adaptFlag(bukkitFlag));
        }
    }

    @Override
    public void convertToBukkit(me.illusion.skyblockcore.server.item.stack.meta.ItemMeta platformMeta, ItemMeta bukkitMeta) {
        bukkitMeta.setLore(platformMeta.getLore());
        bukkitMeta.setDisplayName(platformMeta.getDisplayName());
        bukkitMeta.setCustomModelData(platformMeta.getCustomModelData());

        for (me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag platformFlag : platformMeta.getFlags()) {
            bukkitMeta.addItemFlags(adaptFlag(platformFlag));
        }
    }

    private me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag adaptFlag(ItemFlag bukkitFlag) {
        return switch (bukkitFlag) {
            case HIDE_ENCHANTS -> me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag.HIDE_ENCHANTS;
            case HIDE_ATTRIBUTES -> me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag.HIDE_ATTRIBUTES;
            case HIDE_UNBREAKABLE -> me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag.HIDE_UNBREAKABLE;
            default -> throw new IllegalArgumentException("No adapter found for " + bukkitFlag.name());
        };
    }

    private ItemFlag adaptFlag(me.illusion.skyblockcore.server.item.stack.meta.data.ItemFlag platformFlag) {
        return switch (platformFlag) {
            case HIDE_ENCHANTS -> ItemFlag.HIDE_ENCHANTS;
            case HIDE_ATTRIBUTES -> ItemFlag.HIDE_ATTRIBUTES;
            case HIDE_UNBREAKABLE -> ItemFlag.HIDE_UNBREAKABLE;
            default -> throw new IllegalArgumentException("No adapter found for " + platformFlag.name());
        };
    }
}
