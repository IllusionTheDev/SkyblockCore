package me.illusion.skyblockcore.server.item.stack;

import java.util.function.UnaryOperator;
import me.illusion.skyblockcore.server.item.MinecraftItem;
import me.illusion.skyblockcore.server.item.stack.meta.ItemMeta;

public class MinecraftItemStack {

    private final MinecraftItem item;
    private ItemMeta meta;

    private int amount;

    private MinecraftItemStack(MinecraftItem item, int amount) {
        this.item = item;
        this.amount = amount;

        this.meta = item.createItemMeta();
    }

    public MinecraftItemStack(MinecraftItem item) {
        this(item, 1);
    }

    public static MinecraftItemStack create(MinecraftItem item, int amount) {
        return new MinecraftItemStack(item, amount);
    }

    public static MinecraftItemStack create(MinecraftItem item) {
        return new MinecraftItemStack(item);
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public void modifyMeta(UnaryOperator<ItemMeta> operator) {
        meta = operator.apply(meta);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isSimilar(MinecraftItemStack other) {
        return item == other.item && meta.equals(other.meta);
    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
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
