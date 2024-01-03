package me.illusion.skyblockcore.server.inventory.platform;

import me.illusion.skyblockcore.server.inventory.NamedMenu;
import me.illusion.skyblockcore.server.inventory.type.MenuType;

public interface SkyblockInventoryFactory {

    NamedMenu createMenu(MenuType type, String title);

}
