package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRequestVisitorIsland;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRespondVisitorIsland;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class IslandVisitationPacketHandler implements PacketHandler<PacketRequestVisitorIsland> {

    private final SkyblockBungeePlugin main;

    public IslandVisitationPacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketRequestVisitorIsland packet) {
        UUID requestId = packet.getPacketId();
        UUID IslandId = packet.getIslandId();

        CompletableFuture<String> matchedServer = main.getPlayerFinder().getLoadedIslandServer(IslandId);

        matchedServer.thenAccept(server -> {
            PacketRespondVisitorIsland response = new PacketRespondVisitorIsland(packet.getServerName(), server, requestId);
            main.getPacketManager().send(response);
        });
    }
}
