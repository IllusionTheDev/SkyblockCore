package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {

    private final SkyblockPlugin main;

    public DeathListener(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onDeath(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        try {
            Location center = main.getPlayerManager().get(player).getIslandCenter();
            player.teleport(center);
        } catch (Exception e) {
            ExceptionLogger.log(e);
        }

    }
}
