package me.illusion.skyblockcore.spigot.inventory.container;

import me.illusion.skyblockcore.server.event.container.MenuOpenEvent;
import me.illusion.skyblockcore.server.inventory.ContainerMetadata;
import me.illusion.skyblockcore.server.inventory.NamedMenu;
import me.illusion.skyblockcore.server.inventory.data.ContainerView;
import me.illusion.skyblockcore.server.inventory.type.MenuType;
import me.illusion.skyblockcore.server.item.stack.MinecraftItemStack;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BukkitNamedMenu implements NamedMenu {

    private final Inventory bukkitInventory;
    private final SkyblockSpigotPlugin platform;
    private final String title;
    private final MenuType type;

    public BukkitNamedMenu(SkyblockSpigotPlugin platform, MenuType type, String title) {
        this.type = type;
        this.title = title;
        this.platform = platform;

        bukkitInventory = Bukkit.createInventory(null, type.getMetadata().getSize(), title);
    }

    public static BukkitNamedMenu create(SkyblockSpigotPlugin platform, MenuType type, String title) {
        return new BukkitNamedMenu(platform, type, title);
    }

    @Override
    public MinecraftItemStack getItem(int slot) {
        return SkyblockBukkitAdapter.adapt(bukkitInventory.getItem(slot));
    }

    @Override
    public void setItem(int slot, MinecraftItemStack item) {
        bukkitInventory.setItem(slot, SkyblockBukkitAdapter.adapt(item));
    }

    @Override
    public ContainerMetadata getMetadata() {
        return type.getMetadata();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void open(SkyblockPlayer player) {
        Player bukkitPlayer = SkyblockBukkitAdapter.adapt(player);
        bukkitPlayer.openInventory(bukkitInventory);

        ContainerView view = ContainerView.createView(this.title, this, player);
        platform.getEventManager().callEvent(new MenuOpenEvent(view));
    }

    public Inventory getBukkitInventory() {
        return bukkitInventory;
    }
}
