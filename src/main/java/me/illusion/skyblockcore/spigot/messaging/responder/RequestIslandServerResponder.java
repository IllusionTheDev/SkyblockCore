package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRespondIslandServer;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestIslandServer;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

import java.util.UUID;

public class RequestIslandServerResponder implements PacketHandler<PacketRequestIslandServer> {

    private final SkyblockPlugin main;

    public RequestIslandServerResponder(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketRequestIslandServer packet) {
        UUID islandId = packet.getIslandId();

        boolean found = main.getIslandManager().getLoadedIslandFromId(islandId).isPresent();
        byte IslandCount = main.getIslandManager().getIslandCount();
        byte maxIslandCount = main.getIslandManager().getMaxCapacity();

        PacketRespondIslandServer response = new PacketRespondIslandServer(islandId, found, IslandCount, maxIslandCount);
        main.getPacketManager().send(response);
    }
}
