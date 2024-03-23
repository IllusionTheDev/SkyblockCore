package me.illusion.skyblockcore.server.network.complex.communication.listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.packet.channel.PacketChannel;
import me.illusion.skyblockcore.common.packet.processing.PacketSubscriber;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.server.network.complex.communication.packet.response.PacketResponseIslandTeleport;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * Handles island teleport requests. This will keep a 30 second cache of player requests.
 */
public class TeleportRequestPacketHandler implements PacketSubscriber<PacketRequestIslandTeleport> {

    private final ComplexSkyblockNetwork network;

    private final Map<UUID, UUID> teleportRequests = new ConcurrentHashMap<>(); // TODO: Make a cache out of this. I don't want to shade guava yet.

    public TeleportRequestPacketHandler(ComplexSkyblockNetwork network) {
        this.network = network;
        network.getEventManager().subscribe(SkyblockPlayerJoinEvent.class, this::onJoin);
    }

    @Override
    public void onReceive(String sourceChannel, PacketRequestIslandTeleport packet) {
        UUID playerId = packet.getPlayerId();
        UUID islandId = packet.getIslandId();

        SkyblockIsland loadedIsland = network.getIslandManager().getLoadedIsland(islandId);

        boolean allowed = loadedIsland != null;

        PacketResponseIslandTeleport response = new PacketResponseIslandTeleport(playerId, allowed);

        network
            .getCommunicationsHandler()
            .getPacketManager()
            .sendPacket(PacketChannel.individual(packet.getOriginServer()), response);

        // Internal logic

        if (allowed) {
            teleportRequests.put(playerId, islandId);
        }
    }

    private void onJoin(SkyblockPlayerJoinEvent event) {
        SkyblockPlayer player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        UUID islandId = teleportRequests.get(playerId);

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
