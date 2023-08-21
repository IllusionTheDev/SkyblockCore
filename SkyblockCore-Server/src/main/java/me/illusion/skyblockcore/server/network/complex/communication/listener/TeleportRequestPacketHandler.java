package me.illusion.skyblockcore.server.network.complex.communication.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.illusion.skyblockcore.common.communication.packet.PacketHandler;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.server.network.complex.communication.packet.response.PacketResponseIslandTeleport;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * Handles island teleport requests. This will keep a 30 second cache of player requests.
 */
public class TeleportRequestPacketHandler implements PacketHandler<PacketRequestIslandTeleport> {

    private final ComplexSkyblockNetwork network;

    private final Cache<UUID, UUID> teleportRequests = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build();

    public TeleportRequestPacketHandler(ComplexSkyblockNetwork network) {
        this.network = network;
        network.getEventManager().subscribe(SkyblockPlayerJoinEvent.class, this::onJoin);
    }

    @Override
    public void onReceive(PacketRequestIslandTeleport packet) {
        UUID playerId = packet.getPlayerId();
        UUID islandId = packet.getIslandId();

        SkyblockIsland loadedIsland = network.getIslandManager().getLoadedIsland(islandId);

        boolean allowed = loadedIsland != null;

        PacketResponseIslandTeleport response = new PacketResponseIslandTeleport(playerId, allowed);

        network.getCommunicationsHandler().getPacketManager().send(packet.getOriginServer(), response);

        // Internal logic

        if (allowed) {
            teleportRequests.put(playerId, islandId);
        }
    }

    private void onJoin(SkyblockPlayerJoinEvent event) {
        SkyblockPlayer player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        UUID islandId = teleportRequests.getIfPresent(playerId);

        if (islandId == null) {
            return;
        }

        SkyblockIsland island = network.getIslandManager().getLoadedIsland(islandId);

        if (island == null) { // The island unloaded after the request was sent, odd
            // TODO: Rejection logic
            return;
        }

        player.teleport(island.getCenter());
    }
}
