package me.illusion.skyblockcore.proxy;

import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.SkyblockServerComparatorRegistry;
import me.illusion.skyblockcore.proxy.matchmaking.data.SkyblockServerMatchmaker;

/**
 * Represents the platform SkyblockCore-Proxy is running on.
 */
public interface SkyblockProxyPlatform extends SkyblockPlatform {

    /**
     * Gets the matchmaker for this platform.
     *
     * @return the matchmaker for this platform.
     */
    SkyblockServerMatchmaker getMatchmaker();

    /**
     * Gets the comparator registry for this platform.
     *
     * @return the comparator registry for this platform.
     */
    SkyblockServerComparatorRegistry getMatchmakerComparatorRegistry();

}
