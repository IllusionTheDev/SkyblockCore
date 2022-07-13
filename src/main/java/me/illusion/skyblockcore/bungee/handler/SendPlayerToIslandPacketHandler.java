package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.bungee.data.PlayerFinder;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketTeleportPlayerToIsland;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestTeleportPlayerToIsland;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class SendPlayerToIslandPacketHandler implements PacketHandler<PacketTeleportPlayerToIsland> {

    private final SkyblockBungeePlugin main;

    public SendPlayerToIslandPacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketTeleportPlayerToIsland packet) {
        UUID playerId = packet.getPlayerId();

        PlayerFinder playerFinder = main.getPlayerFinder();
        ProxyServer proxy = ProxyServer.getInstance();

        playerFinder.getLoadedIslandServer(packet.getIslandId()).thenAccept((server) -> {
            ProxiedPlayer player = proxy.getPlayer(playerId);

            if (player == null)
                return;

            PacketRequestTeleportPlayerToIsland request = new PacketRequestTeleportPlayerToIsland(server, playerId, packet.getOriginalPlayerData(), packet.getIslandId());
            main.getPacketManager().send(request)
                    .thenRun(() -> {
                        ServerInfo info = proxy.getServerInfo(server);
                        player.connect(info);
                    });
        });
    }
}
