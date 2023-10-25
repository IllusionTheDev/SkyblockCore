package me.illusion.skyblockcore.server.item.stack;

import java.util.List;

public interface ItemMeta {

    String getDisplayName();
    void setDisplayName(String displayName);

    List<String> getLore();
    void setLore(List<String> lore);

}
