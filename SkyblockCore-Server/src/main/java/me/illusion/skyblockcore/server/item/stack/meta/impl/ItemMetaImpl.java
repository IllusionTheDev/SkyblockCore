package me.illusion.skyblockcore.server.item.stack.meta.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.illusion.skyblockcore.server.item.stack.ItemMeta;
import me.illusion.skyblockcore.server.item.stack.meta.ItemFlag;

public class ItemMetaImpl implements ItemMeta {

    private String displayName;
    private final List<String> lore = new ArrayList<>();
    private final Set<ItemFlag> flags = new HashSet<>();
    private int modelData;

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public void setLore(List<String> lore) {
        this.lore.clear();
        this.lore.addAll(lore);
    }

    @Override
    public int getCustomModelData() {
        return modelData;
    }

    @Override
    public void setCustomModelData(int customModelData) {
        this.modelData = customModelData;
    }

    @Override
    public boolean hasFlag(ItemFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public void addFlag(ItemFlag flag) {
        flags.add(flag);
    }

    @Override
    public void removeFlag(ItemFlag flag) {
        flags.remove(flag);
    }

}
