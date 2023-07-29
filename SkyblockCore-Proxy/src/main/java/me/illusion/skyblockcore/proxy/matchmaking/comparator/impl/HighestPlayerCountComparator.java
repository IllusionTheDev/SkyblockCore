package me.illusion.skyblockcore.proxy.matchmaking.comparator.impl;

import me.illusion.skyblockcore.proxy.instance.ProxyServerData;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator;

/**
 * Compares two servers based on the amount of players they have. The server with the most players will be chosen.
 */
public class HighestPlayerCountComparator implements ServerDataComparator {

    @Override
    public int compare(ProxyServerData proxyServerData, ProxyServerData t1) {
        return Integer.compare(t1.getPlayerCount(), proxyServerData.getPlayerCount());
    }

    @Override
    public String getName() {
        return "least-players";
    }
}
