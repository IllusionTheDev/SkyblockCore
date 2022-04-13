package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.bungee.data.PlayerFinder;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketTeleportPlayerToIsland;
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
        ProxiedPlayer player = main.getProxy().getPlayer(playerId);

        playerFinder.requestIslandServer(playerId).whenComplete((servername, thr) -> {
            if (servername == null) // Assign available server if no members are online
                servername = playerFinder.getAvailableServer();

            if (servername == null) // If no space found
                return;

            ServerInfo targetServer = main.getProxy().getServerInfo(servername);

            player.connect(targetServer);
        });
    }
}
