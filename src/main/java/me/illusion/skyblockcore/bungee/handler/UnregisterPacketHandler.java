package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketUnregisterServer;

public class UnregisterPacketHandler implements PacketHandler<PacketUnregisterServer> {

    private final SkyblockBungeePlugin main;

    public UnregisterPacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketUnregisterServer packet) {
        main.getPlayerFinder().unregisterServer(packet.getServerName());
    }
}
