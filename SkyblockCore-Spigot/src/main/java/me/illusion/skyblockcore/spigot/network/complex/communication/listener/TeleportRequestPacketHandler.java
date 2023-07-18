package me.illusion.skyblockcore.spigot.network.complex.communication.listener;

import java.util.UUID;
import me.illusion.skyblockcore.common.communication.packet.PacketHandler;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.response.PacketResponseIslandTeleport;

public class TeleportRequestPacketHandler implements PacketHandler<PacketRequestIslandTeleport> {

    private final ComplexSkyblockNetwork network;

    public TeleportRequestPacketHandler(ComplexSkyblockNetwork network) {
        this.network = network;
    }

    @Override
    public void onReceive(PacketRequestIslandTeleport packet) {
        UUID playerId = packet.getPlayerId();
        UUID islandId = packet.getIslandId();

        Island loadedIsland = network.getIslandManager().getLoadedIsland(islandId);

        boolean allowed = loadedIsland != null;

        PacketResponseIslandTeleport response = new PacketResponseIslandTeleport(playerId, allowed);

        network.getCommunicationsHandler().getPacketManager().send(packet.getOriginServer(), response);
    }
}
