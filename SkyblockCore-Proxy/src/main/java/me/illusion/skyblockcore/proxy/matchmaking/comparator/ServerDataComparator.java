package me.illusion.skyblockcore.proxy.matchmaking.comparator;

import java.util.Comparator;
import me.illusion.skyblockcore.proxy.instance.ProxyServerData;

/**
 * Represents a comparator for Skyblock servers. The comparator is responsible for comparing two servers and determining which one is more suitable for a player
 * to join.
 */
public interface ServerDataComparator extends Comparator<ProxyServerData> {

    /**
     * Gets the name of this comparator.
     *
     * @return the name of this comparator.
     */
    String getName();

}
