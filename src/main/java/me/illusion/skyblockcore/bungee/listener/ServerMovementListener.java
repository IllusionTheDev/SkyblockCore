package me.illusion.skyblockcore.bungee.listener;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestIslandUnload;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;
import java.util.UUID;

public class ServerMovementListener implements Listener {

    private final SkyblockBungeePlugin main;

    public ServerMovementListener(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onServerMove(PlayerDisconnectEvent event) {
        Set<String> ids = main.getPlayerFinder().getSkyblockServerNames();
        ServerInfo server = event.getPlayer().getServer().getInfo();

        String serverId = server.getName();

        if (!ids.contains(serverId)) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();

        unload(playerId);
    }

    private void unload(UUID playerId) {

        main.getStorageHandler().get(playerId, "ISLAND").thenAccept((data) -> {
            if (data == null) {
                return;
            }

            IslandData islandData = (IslandData) data;
            UUID islandId = islandData.getId();
            main.getPacketManager().send(new PacketRequestIslandUnload(islandId));
        });
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        Set<String> ids = main.getPlayerFinder().getSkyblockServerNames();

        ServerInfo from = event.getFrom();

        if (from == null)
            return;

        ServerInfo server = event.getPlayer().getServer().getInfo();

        String oldServerId = from.getName();
        String serverId = server.getName();

        if (!ids.contains(serverId))
            if (!ids.contains(oldServerId))
                return;


        UUID playerId = event.getPlayer().getUniqueId();
        unload(playerId);
    }
}
