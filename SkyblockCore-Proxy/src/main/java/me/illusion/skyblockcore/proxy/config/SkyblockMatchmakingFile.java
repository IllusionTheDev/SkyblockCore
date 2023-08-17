package me.illusion.skyblockcore.proxy.config;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;

/**
 * Represents the matchmaking configuration file.
 */
public class SkyblockMatchmakingFile extends AbstractConfiguration {

    private final String preferredComparator;

    public SkyblockMatchmakingFile(SkyblockProxyPlatform platform) {
        super(platform, "matchmaking.yml");

        preferredComparator = getConfiguration().getString("preferred-comparator", "least-islands");
    }

    /**
     * Obtains the preferred comparator name, used to get the {@link me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator}
     *
     * @return The preferred comparator name.
     */
    public String getPreferredComparator() {
        return preferredComparator;
    }
}
