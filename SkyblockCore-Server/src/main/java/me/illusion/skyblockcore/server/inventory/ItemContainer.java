package me.illusion.skyblockcore.server.inventory;

import me.illusion.skyblockcore.server.item.stack.MinecraftItemStack;

public interface ItemContainer {

    MinecraftItemStack getItem(int slot);

    void setItem(int slot, MinecraftItemStack item);

    ContainerMetadata getMetadata();

}
