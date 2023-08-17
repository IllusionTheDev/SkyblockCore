package me.illusion.skyblockcore.proxy.config;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;

public class SkyblockMatchmakingFile extends AbstractConfiguration {

    private final String preferredComparator;

    public SkyblockMatchmakingFile(SkyblockProxyPlatform platform) {
        super(platform, "matchmaking.yml");

        preferredComparator = getConfiguration().getString("preferred-comparator", "least-islands");
    }

    public String getPreferredComparator() {
        return preferredComparator;
    }
}
