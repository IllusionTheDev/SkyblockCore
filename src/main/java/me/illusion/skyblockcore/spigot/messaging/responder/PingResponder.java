package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketPong;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketPing;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

public class PingResponder implements PacketHandler<PacketPing> {

    private final SkyblockPlugin main;

    public PingResponder(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketPing packet) {
        PacketPong response = new PacketPong(main.getIslandManager().getIslandCount(), main.getIslandManager().getMaxCapacity());
        main.getPacketManager().send(response);
    }
}
