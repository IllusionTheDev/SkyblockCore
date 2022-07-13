package me.illusion.skyblockcore.spigot.messaging.responder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestTeleportPlayerToIsland;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandLoadEvent;
import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeleportIslandResponder implements PacketHandler<PacketRequestTeleportPlayerToIsland>, Listener {

    private final SkyblockPlugin main;
    private final Cache<UUID, UUID> teleportCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    public TeleportIslandResponder(SkyblockPlugin main) {
        this.main = main;

        Bukkit.getPluginManager().registerEvents(this, main); // To keep the main simple, I'll register the listener here
    }

    @Override
    public void onReceive(PacketRequestTeleportPlayerToIsland packet) {
        main.getIslandManager().loadRemoteIsland(packet.getOriginalPlayerData());

        UUID playerId = packet.getPlayerId();
        UUID islandId = packet.getIslandId();

        Player player = Bukkit.getPlayer(playerId);

        if (player != null) {
            Island island = main.getIslandManager().getIsland(islandId);

            if (island != null) {
                island.teleport(player);
                return;
            }
        }

        teleportCache.put(playerId, islandId);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        UUID islandId = teleportCache.getIfPresent(playerId);

        if (islandId != null) {
            teleportCache.invalidate(playerId);

            Island island = main.getIslandManager().getIsland(islandId);

            if (island != null) {
                island.teleport(event.getPlayer());
            }
        }
    }

    @EventHandler
    private void onislandLoad(IslandLoadEvent event) {
        Island island = event.getIsland();
        UUID islandId = island.getData().getId();

        for (Map.Entry<UUID, UUID> entry : teleportCache.asMap().entrySet()) {
            UUID playerId = entry.getKey();
            UUID targetisland = entry.getValue();

            if (!islandId.equals(targetisland))
                continue;

            Player player = Bukkit.getPlayer(playerId);

            if (player != null) {
                island.teleport(player);
            }
        }
    }
}
