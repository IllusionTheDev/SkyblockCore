package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.PacketManager;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRespondIslandServer;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestIslandServer;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

import java.util.UUID;

public class PacketResponder {

    private final SkyblockPlugin main;

    public PacketResponder(SkyblockPlugin main) {
        this.main = main;
        register();
    }

    private void register() {
        PacketManager packetManager = main.getPacketManager();

        packetManager.subscribe(PacketRequestIslandServer.class, new PacketHandler<PacketRequestIslandServer>() {
            @Override
            public void onReceive(PacketRequestIslandServer packet) {
                UUID islandId = packet.getIslandId();

                boolean found = main.getIslandManager().getLoadedIslandFromId(islandId).isPresent();
                byte islandCount = main.getIslandManager().getIslandCount();
                byte maxIslandCount = main.getIslandManager().getMaxCapacity();

                PacketRespondIslandServer response = new PacketRespondIslandServer(islandId, found, islandCount, maxIslandCount);
                main.getPacketManager().send(response);
            }
        });
    }
}
