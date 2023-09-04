package me.illusion.skyblockcore.bungee.command;

import java.util.UUID;
import me.illusion.skyblockcore.proxy.audience.SkyblockProxyPlayerAudience;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Represents a SkyblockAudience for any BungeeCord ProxiedPlayer
 */
public class BungeeSkyblockPlayerAudience implements SkyblockProxyPlayerAudience {

    private final UUID playerId;

    public BungeeSkyblockPlayerAudience(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return getPlayer().hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public UUID getUniqueId() {
        return playerId;
    }

    @Override
    public void connect(String server) {
        getPlayer().connect(ProxyServer.getInstance().getServerInfo(server));
    }

    private ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(playerId);
    }
}
