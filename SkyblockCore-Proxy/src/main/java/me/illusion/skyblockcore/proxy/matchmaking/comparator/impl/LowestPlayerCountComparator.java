package me.illusion.skyblockcore.proxy.matchmaking.comparator.impl;

import me.illusion.skyblockcore.proxy.instance.ProxyServerData;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator;

/**
 * Compares two servers based on the amount of players they have. The server with the least players will be chosen.
 */
public class LowestPlayerCountComparator implements ServerDataComparator {

    @Override
    public int compare(ProxyServerData proxyServerData, ProxyServerData t1) {
        return Integer.compare(proxyServerData.getPlayerCount(), t1.getPlayerCount());
    }

    @Override
    public String getName() {
        return "most-players";
    }
}
