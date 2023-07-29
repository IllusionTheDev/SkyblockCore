package me.illusion.skyblockcore.proxy.matchmaking.comparator.impl;

import me.illusion.skyblockcore.proxy.instance.ProxyServerData;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator;

/**
 * Compares two servers based on the amount of islands they have. The server with the most islands will be chosen.
 */
public class HighestIslandCountComparator implements ServerDataComparator {

    @Override
    public String getName() {
        return "most-islands";
    }

    @Override
    public int compare(ProxyServerData proxyServerData, ProxyServerData t1) {
        return Integer.compare(t1.getIslandCount(), proxyServerData.getIslandCount());
    }
}
