package me.illusion.skyblockcore.server.config;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public class IslandManagerConfiguration extends AbstractConfiguration {

    private final String provider;

    public IslandManagerConfiguration(SkyblockPlatform platform) {
        super(platform, "island-settings.yml");
        this.provider = getString("provider");
    }

    public String getProvider() {
        return provider;
    }

    public ConfigurationSection getProviderConfig() {
        return getSection("provider");
    }
}
