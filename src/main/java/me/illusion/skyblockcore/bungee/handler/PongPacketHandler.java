package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketPong;

public class PongPacketHandler implements PacketHandler<PacketPong> {

    private final SkyblockBungeePlugin main;

    public PongPacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketPong packet) {
        main.getPlayerFinder().registerServer(packet.getServerName(), packet.getIslandCount(), packet.getIslandCapacity());
    }
}
