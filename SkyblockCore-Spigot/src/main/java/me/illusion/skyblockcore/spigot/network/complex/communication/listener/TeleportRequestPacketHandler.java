package me.illusion.skyblockcore.spigot.network.complex.communication.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.illusion.skyblockcore.common.communication.packet.PacketHandler;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.response.PacketResponseIslandTeleport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles island teleport requests. This will keep a 30 second cache of player requests.
 */
public class TeleportRequestPacketHandler implements PacketHandler<PacketRequestIslandTeleport>, Listener {

    private final ComplexSkyblockNetwork network;

    private final Cache<UUID, UUID> teleportRequests = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build();

    public TeleportRequestPacketHandler(ComplexSkyblockNetwork network) {
        this.network = network;
        network.registerListener(this);
    }

    @Override
    public void onReceive(PacketRequestIslandTeleport packet) {
        UUID playerId = packet.getPlayerId();
        UUID islandId = packet.getIslandId();

        Island loadedIsland = network.getIslandManager().getLoadedIsland(islandId);

        boolean allowed = loadedIsland != null;

        PacketResponseIslandTeleport response = new PacketResponseIslandTeleport(playerId, allowed);

        network.getCommunicationsHandler().getPacketManager().send(packet.getOriginServer(), response);

        // Internal logic

        if (allowed) {
            teleportRequests.put(playerId, islandId);
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        UUID islandId = teleportRequests.getIfPresent(playerId);

        if (islandId == null) {
            return;
        }

        Island island = network.getIslandManager().getLoadedIsland(islandId);

        if (island == null) { // The island unloaded after the request was sent, odd
            // TODO: Rejection logic
            return;
        }

        player.teleport(island.getCenter());
    }
}
