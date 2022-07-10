package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxytoproxy.PacketRedirectPacket;

public class RedirectPacketHandler implements PacketHandler<PacketRedirectPacket> {

    private final SkyblockBungeePlugin main;

    public RedirectPacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketRedirectPacket packet) {
        byte[] bytes = packet.getBytes();

        main.getPacketManager().read(bytes);
    }
}
