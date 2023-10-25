package me.illusion.skyblockcore.server.item.stack.meta.impl;

import java.util.ArrayList;
import java.util.List;
import me.illusion.skyblockcore.server.item.stack.ItemMeta;

public class ItemMetaImpl implements ItemMeta {

    private String displayName;
    private final List<String> lore = new ArrayList<>();
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

}
