package me.illusion.skyblockcore.bungee.handler;


import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRespondInvite;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketInviteResponse;

public class InviteResponseHandler implements PacketHandler<PacketRespondInvite> {

    private final SkyblockBungeePlugin main;

    public InviteResponseHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketRespondInvite packet) {
        PacketInviteResponse response = new PacketInviteResponse(packet.getInvite(), packet.getResponse());

        main.getPacketManager().send(response);
    }
}
