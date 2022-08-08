package me.illusion.skyblockcore.bungee.handler;


import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketInvitePlayer;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketIndicateInvite;

public class InvitePacketHandler implements PacketHandler<PacketInvitePlayer> {

    private final SkyblockBungeePlugin main;

    public InvitePacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketInvitePlayer packet) {
        PacketIndicateInvite invite = new PacketIndicateInvite(packet.getInvite());

        main.getPacketManager().send(invite);
    }
}
