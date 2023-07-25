package me.illusion.skyblockcore.spigot.network.simple.profile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.profile.AbstractSkyblockProfileCache;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.spigot.event.player.SkyblockPlayerQuitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * A simple implementation of {@link AbstractSkyblockProfileCache} that uses Bukkit events to notify when a player joins or quits the server, and when they
 * switch profiles.
 */
public class SimpleProfileCache extends AbstractSkyblockProfileCache implements Listener {

    public SimpleProfileCache(SkyblockSpigotPlugin plugin) {
        super(plugin.getDatabaseRegistry().getChosenDatabase());
    }

    @Override
    public CompletableFuture<Void> saveProfileId(UUID playerId, UUID newProfileId) {
        UUID oldProfileId = getCachedProfileId(playerId);

        return super.saveProfileId(playerId, newProfileId).thenRun(() -> {
            Player player = Bukkit.getPlayer(playerId);

            if (player == null) {
                return;
            }

            SkyblockPlayerQuitEvent event = new SkyblockPlayerQuitEvent(player, oldProfileId);
            Bukkit.getPluginManager().callEvent(event);

            SkyblockPlayerJoinEvent joinEvent = new SkyblockPlayerJoinEvent(player, newProfileId);
            Bukkit.getPluginManager().callEvent(joinEvent);
        });
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        cacheProfileId(playerId).thenAccept(profileId -> {
            SkyblockPlayerJoinEvent joinEvent = new SkyblockPlayerJoinEvent(player, profileId);
            Bukkit.getPluginManager().callEvent(joinEvent);
        });
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        UUID playerId = player.getUniqueId();
        UUID cachedProfileId = getCachedProfileId(playerId);

        if (cachedProfileId == null) {
            return;
        }

        SkyblockPlayerQuitEvent quitEvent = new SkyblockPlayerQuitEvent(player, cachedProfileId);
        Bukkit.getPluginManager().callEvent(quitEvent);

        deleteFromCache(playerId);
    }
}
