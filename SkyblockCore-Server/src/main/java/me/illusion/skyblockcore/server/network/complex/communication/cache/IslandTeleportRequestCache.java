package me.illusion.skyblockcore.server.network.complex.communication.cache;

import java.util.UUID;
import me.illusion.skyblockcore.common.packet.processing.PacketSubscriber;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.network.complex.communication.data.Request;
import me.illusion.skyblockcore.server.network.complex.communication.packet.response.PacketResponseIslandTeleport;

public class IslandTeleportRequestCache extends RequestCache<Boolean> implements PacketSubscriber<PacketResponseIslandTeleport> {

    public IslandTeleportRequestCache(ComplexSkyblockNetwork network) {
        super(network.getPlatform().getScheduler(), Time.seconds(5));
        network.getCommunicationsHandler().getPacketManager().subscribe(PacketResponseIslandTeleport.class, this);
    }

    @Override
    public void onReceive(String sourceId, PacketResponseIslandTeleport packet) {
        completeRequest(packet.getPlayerId(), packet.isAllowed());
    }

    public Request<Boolean> createRequest(UUID playerId, UUID islandId) {
        return super.createRequest(playerId);
    }
}
