package me.illusion.skyblockcore.bungee.instance;

import java.util.Collection;
import java.util.UUID;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;
import me.illusion.skyblockcore.proxy.instance.ProxyServerData;
import me.illusion.skyblockcore.proxy.matchmaking.data.AbstractSkyblockServerMatchmaker;

/**
 * Bungee factory for {@link ProxyServerData}.
 */
public class BungeeSkyblockMatchmaker extends AbstractSkyblockServerMatchmaker {

    public BungeeSkyblockMatchmaker(SkyblockProxyPlatform platform) {
        super(platform);
    }

    @Override
    public ProxyServerData createData(String server, Collection<UUID> islandIds) {
        return new BungeeProxyServerData(server, islandIds);
    }
}
