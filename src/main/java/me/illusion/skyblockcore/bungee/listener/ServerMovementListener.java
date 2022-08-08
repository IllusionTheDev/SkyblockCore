package me.illusion.skyblockcore.bungee.listener;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.bungee.data.PlayerFinder;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestIslandUnload;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
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
        Server server = event.getPlayer().getServer();

        if (server == null) {
            return;
        }

        ServerInfo info = event.getPlayer().getServer().getInfo();

        String serverId = info.getName();

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

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        Set<String> ids = main.getPlayerFinder().getSkyblockServerNames();
        ServerInfo server = event.getTarget();

        String serverId = server.getName();

        if (!ids.contains(serverId))
            return;

        auth(event);
    }

    private void auth(ServerConnectEvent event) {
        PlayerFinder playerFinder = main.getPlayerFinder();
        ProxiedPlayer player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String serverId = event.getTarget().getName();

        if (!playerFinder.isAuth(playerId)) {
            System.out.println("Player " + player.getName() + " is not authorized to enter Islands");
            event.setCancelled(true);
            auth(player);
            return;
        }

        String authId = playerFinder.getAuthServer(playerId);

        if (!authId.equals(serverId)) {
            System.out.println("[Housing] " + player.getName() + " is not on the correct server for their auth. Expected " + authId + " but got " + serverId);
            event.setCancelled(true);
            auth(player);
        }

    }

    private void auth(ProxiedPlayer player) {
        PlayerFinder playerFinder = main.getPlayerFinder();

        System.out.println("re-Authing " + player.getName());

        playerFinder.requestIslandServer(player.getUniqueId()).whenComplete((servername, thr) -> {
            System.out.println("Requested Island server: " + servername);

            if (servername == null) {// If no space found
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "There are no available housing servers."));
                return;
            }

            if (servername.equalsIgnoreCase("NOT_AUTH")) {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are not authorized to enter housing."));
                return;
            }

            playerFinder.authPlayer(player.getUniqueId(), servername);
            ServerInfo targetServer = main.getProxy().getServerInfo(servername);
            player.connect(targetServer);
        });
    }
}