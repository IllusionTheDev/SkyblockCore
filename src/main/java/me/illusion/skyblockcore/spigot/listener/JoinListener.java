package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.data.SkyblockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private final SkyblockPlugin main;

    public JoinListener(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        String worldName = world.getName();
        UUID playerId = player.getUniqueId();

        if (main.getWorldManager().isSkyblockWorld(worldName))
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());

        SkyblockPlayer skyblockPlayer = new SkyblockPlayer(main, playerId);

        /*
        new ScheduleBuilder(main)
                .in(2)
                .ticks()
                .run(() -> main.getBungeeMessaging().sendData(p))
                .sync()
                .start();

         */
    }
}
