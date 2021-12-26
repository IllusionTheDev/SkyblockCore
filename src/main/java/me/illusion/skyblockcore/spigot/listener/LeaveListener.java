package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Bukkit;
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

        p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        try {
            main.getPlayerManager().get(p).save();

            p.getInventory().clear();
            main.getPlayerManager().unregister(p.getUniqueId());

        } catch (Exception ex) {
            ExceptionLogger.log(ex);
        }


    }
}
