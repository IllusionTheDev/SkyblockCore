package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxy.proxy.request.PacketRequestMessageSend;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class MessagePacketHandler implements PacketHandler<PacketRequestMessageSend> {

    @Override
    public void onReceive(PacketRequestMessageSend packet) {
        UUID uuid = packet.getUuid();
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        if (player == null)
            return;

        player.sendMessage(packet.getMessage());
    }
}
