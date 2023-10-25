package me.illusion.skyblockcore.server.item.stack;

import java.util.List;
import me.illusion.skyblockcore.server.item.stack.meta.ItemFlag;

public interface ItemMeta {

    String getDisplayName();
    void setDisplayName(String displayName);

    List<String> getLore();
    void setLore(List<String> lore);

    int getCustomModelData();
    void setCustomModelData(int customModelData);

    boolean hasFlag(ItemFlag flag);
    void addFlag(ItemFlag flag);
    void removeFlag(ItemFlag flag);

}
