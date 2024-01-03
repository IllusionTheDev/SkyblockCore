package me.illusion.skyblockcore.spigot.registries;

import me.illusion.skyblockcore.common.registry.SimpleRegistry;
import me.illusion.skyblockcore.server.item.MinecraftItem;
import me.illusion.skyblockcore.server.item.MinecraftMaterial;
import me.illusion.skyblockcore.server.item.stack.meta.ItemMeta;
import me.illusion.skyblockcore.spigot.registries.meta.BukkitMetaAdapter;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class BukkitMaterialRegistry extends SimpleRegistry<MinecraftItem> {

    public BukkitMaterialRegistry() {
        super(MinecraftItem.class);

        for (Material material : Material.values()) {
            register(new MaterialItem(material));
        }
    }

    public MinecraftItem getItem(Material material) {
        return get(SkyblockBukkitAdapter.adapt(material.getKey()));
    }

    public Material getMaterial(MinecraftItem item) {
        return Material.getMaterial(item.getKey().toString());
    }

    private static class MaterialItem extends MinecraftMaterial implements MinecraftItem {

        private final Material material;

        protected MaterialItem(Material material) {
            super(SkyblockBukkitAdapter.adapt(material.getKey()));
            this.material = material;
        }

        @Override
        public MinecraftItem attemptCreateItem() {
            return material.isItem() ? this : null;
        }

        @Override
        public MinecraftMaterial getMaterial() {
            return this;
        }

        @Override
        public int getMaxStackSize() {
            return material.getMaxStackSize();
        }

        @Override
        public ItemMeta createItemMeta() {
            org.bukkit.inventory.meta.ItemMeta bukkitMeta = Bukkit.getItemFactory().getItemMeta(material);

            if (bukkitMeta == null) {
                return null;
            }

            return BukkitMetaAdapter.adapt(bukkitMeta);
        }
    }
}
