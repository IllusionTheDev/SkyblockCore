package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.data.SkyblockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final SkyblockPlugin main;

    public JoinListener(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (main.getWorldManager().isSkyblockWorld(p.getWorld().getName()))
            p.teleport(Bukkit.getWorld("world").getSpawnLocation());

        SkyblockPlayer player = new SkyblockPlayer(main, p.getUniqueId());

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
