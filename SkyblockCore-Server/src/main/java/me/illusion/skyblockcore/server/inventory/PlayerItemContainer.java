package me.illusion.skyblockcore.server.inventory;

import me.illusion.skyblockcore.server.inventory.slot.EquipmentSlot;
import me.illusion.skyblockcore.server.item.stack.MinecraftItemStack;

public interface PlayerItemContainer extends ItemContainer {

    void setItem(EquipmentSlot slot, MinecraftItemStack item);

    MinecraftItemStack getItem(EquipmentSlot slot);

    int getHeldItemSlot();

}
