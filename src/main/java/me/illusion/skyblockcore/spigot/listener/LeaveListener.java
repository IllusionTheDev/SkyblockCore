package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final SkyblockPlugin main;

    public LeaveListener(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        main.getPlayerManager().get(p).save();
        p.getInventory().clear();
        main.getPlayerManager().unregister(p.getUniqueId());
    }
}
