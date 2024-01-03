package me.illusion.skyblockcore.server.inventory;

import me.illusion.skyblockcore.server.player.SkyblockPlayer;

public interface NamedMenu extends ItemContainer {

    String getTitle();

    void open(SkyblockPlayer player);

}
