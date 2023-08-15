package me.illusion.skyblockcore.spigot.player;

import java.util.UUID;
import me.illusion.skyblockcore.server.player.AbstractSkyblockPlayerManager;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SkyblockBukkitPlayerManager extends AbstractSkyblockPlayerManager implements Listener {

    public SkyblockBukkitPlayerManager(SkyblockSpigotPlugin plugin) {
        super(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected SkyblockPlayer createPlayer(UUID playerId) {
        return new SkyblockBukkitPlayer(this, playerId);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        handleJoin(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer().getUniqueId());
    }
}
