package me.illusion.skyblockcore.listener;

import me.illusion.skyblockcore.CorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final CorePlugin main;

    public LeaveListener(CorePlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        main.getPlayerManager().get(e.getPlayer()).save();
        e.getPlayer().getInventory().clear();
        main.getPlayerManager().unregister(e.getPlayer().getUniqueId());
    }
}
