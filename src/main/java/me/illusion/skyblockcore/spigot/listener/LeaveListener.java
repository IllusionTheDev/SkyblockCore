package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.data.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class LeaveListener implements Listener {

    private final SkyblockPlugin main;

    public LeaveListener(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        PlayerManager playerManager = main.getPlayerManager();

        try {
            playerManager.get(player).save();
            playerManager.unregister(playerId);

        } catch (Exception ex) {
            ExceptionLogger.log(ex);
        }

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

    }
}
