package me.illusion.skyblockcore.server.item.stack;

import me.illusion.skyblockcore.server.item.MinecraftMaterial;

public class MinecraftItemStack {

    private final MinecraftMaterial material;
    private final ItemMeta meta;

    private int amount;

    public MinecraftItemStack(MinecraftMaterial material, int amount) {
        this.material = material;
        this.amount = amount;

        this.meta = material.createItemMeta();
    }

    public MinecraftItemStack(MinecraftMaterial material) {
        this(material, 1);
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isSimilar(MinecraftItemStack other) {
        return material == other.material && meta.equals(other.meta);
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + meta.hashCode();
        result = 31 * result + amount;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MinecraftItemStack other)) {
            return false;
        }

        if (other == this) {
            return true;
        }

        return isSimilar(other) && amount == other.amount;
    }
}
