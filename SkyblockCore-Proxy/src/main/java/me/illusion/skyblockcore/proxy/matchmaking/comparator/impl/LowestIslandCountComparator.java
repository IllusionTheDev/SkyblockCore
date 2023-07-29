package me.illusion.skyblockcore.proxy.matchmaking.comparator.impl;

import me.illusion.skyblockcore.proxy.instance.ProxyServerData;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator;

/**
 * Compares two servers based on the amount of islands they have. The server with the least islands will be chosen.
 */
public class LowestIslandCountComparator implements ServerDataComparator {

    @Override
    public String getName() {
        return "least-islands";
    }

    @Override
    public int compare(ProxyServerData proxyServerData, ProxyServerData t1) {
        return Integer.compare(proxyServerData.getIslandCount(), t1.getIslandCount());
    }
}
