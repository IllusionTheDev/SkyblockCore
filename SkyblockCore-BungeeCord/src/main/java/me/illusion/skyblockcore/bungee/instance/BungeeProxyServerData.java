package me.illusion.skyblockcore.bungee.instance;

import java.util.Collection;
import java.util.UUID;
import me.illusion.skyblockcore.proxy.instance.ProxyServerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class BungeeProxyServerData implements ProxyServerData {

    private final String serverName;
    private final Collection<UUID> islandIds;

    public BungeeProxyServerData(String serverName, Collection<UUID> islandIds) {
        this.serverName = serverName;
        this.islandIds = islandIds;

    }

    @Override
    public String getName() {
        return serverName;
    }

    @Override
    public int getPlayerCount() {
        return getServer().getPlayers().size();
    }

    @Override
    public int getIslandCount() {
        return islandIds.size();
    }

    private ServerInfo getServer() {
        return ProxyServer.getInstance().getServerInfo(serverName);
    }
}
