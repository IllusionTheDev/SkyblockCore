package me.illusion.skyblockcore.spigot.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.server.event.container.MenuOpenEvent;
import me.illusion.skyblockcore.server.inventory.data.ContainerView;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

public class BukkitInventoryTracker implements Listener {

    private final Map<InventoryView, ContainerView> viewMap = new ConcurrentHashMap<>();

    public BukkitInventoryTracker(SkyblockSpigotPlugin platform) {
        Bukkit.getPluginManager().registerEvents(this, platform);
        platform.getEventManager().subscribe(MenuOpenEvent.class, this::handleOpen);
    }

    private void handleOpen(MenuOpenEvent event) {
        ContainerView view = event.getView();
        SkyblockPlayer player = view.getPlayer();
        Player bukkitPlayer = SkyblockBukkitAdapter.adapt(player);

        if (bukkitPlayer == null) {
            return;
        }

        InventoryView bukkitView = bukkitPlayer.getOpenInventory();
        viewMap.put(bukkitView, view);
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        ContainerView containerView = viewMap.get(view);

        if (containerView == null) {
        }


    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        viewMap.remove(view);
    }

}
