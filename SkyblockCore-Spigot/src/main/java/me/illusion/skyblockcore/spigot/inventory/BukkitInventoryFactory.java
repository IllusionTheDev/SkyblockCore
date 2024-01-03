package me.illusion.skyblockcore.spigot.inventory;

import me.illusion.skyblockcore.server.inventory.NamedMenu;
import me.illusion.skyblockcore.server.inventory.platform.SkyblockInventoryFactory;
import me.illusion.skyblockcore.server.inventory.type.MenuType;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.inventory.container.BukkitNamedMenu;

public class BukkitInventoryFactory implements SkyblockInventoryFactory {

    private final SkyblockSpigotPlugin platform;

    public BukkitInventoryFactory(SkyblockSpigotPlugin platform) {
        this.platform = platform;
    }

    @Override
    public NamedMenu createMenu(MenuType type, String title) {
        return BukkitNamedMenu.create(platform, type, title);
    }
}
