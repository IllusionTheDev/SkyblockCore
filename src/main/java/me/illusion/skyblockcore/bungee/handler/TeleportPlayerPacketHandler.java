package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketTeleportPlayer;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketTeleportPlayerInstance;
import me.illusion.skyblockcore.shared.packet.impl.proxytoproxy.PacketRedirectPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class TeleportPlayerPacketHandler implements PacketHandler<PacketTeleportPlayer> {

    private final SkyblockBungeePlugin main;

    public TeleportPlayerPacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketTeleportPlayer packet) {
        UUID playerId = packet.getUuid();
        String targetServer = packet.getServerName();

        ProxyServer proxy = ProxyServer.getInstance();

        ProxiedPlayer player = proxy.getPlayer(playerId);

        if (player == null) {
            // redirect packet
            PacketRedirectPacket redirectPacket = new PacketRedirectPacket(targetServer, packet);
            main.getPacketManager().send(redirectPacket);
            return;
        }

        String currentName = player.getServer().getInfo().getName();

        if (!currentName.equalsIgnoreCase(targetServer)) {
            ServerInfo targetServerInfo = proxy.getServerInfo(targetServer);
            player.connect(targetServerInfo);
        }


        PacketTeleportPlayerInstance instancePacket = new PacketTeleportPlayerInstance(targetServer, playerId, packet.getSerializedLocation());

        main.getPacketManager().send(instancePacket);
    }

}
