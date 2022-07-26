package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketIndicateInvite;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

public class InviteIndicationResponder implements PacketHandler<PacketIndicateInvite> {

    private final SkyblockPlugin main;

    public InviteIndicationResponder(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketIndicateInvite packet) {
        main.getInviteCache().addInvite(packet.getInvite());
    }
}
