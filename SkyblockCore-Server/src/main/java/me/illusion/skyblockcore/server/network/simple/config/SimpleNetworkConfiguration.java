package me.illusion.skyblockcore.server.network.simple.config;

import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.common.utilities.time.TimeParser;

/**
 * This class is responsible for loading the configuration for the simple skyblock network. It is expected that the configuration file is present in the
 * plugin's data folder.
 */
public class SimpleNetworkConfiguration extends AbstractConfiguration {

    private final String defaultIslandName;
    private final Time unloadDelay;

    public SimpleNetworkConfiguration(SkyblockPlatform platform) {
        super(platform, "network/simple-network.yml");

        defaultIslandName = configuration.getString("island.default-name", "default");
        unloadDelay = parseTime("island.unload-delay");
    }

    private Time parseTime(String path) {
        return TimeParser.parse(configuration.getString(path, "10 minutes"));
    }

    public String getDefaultIslandName() {
        return defaultIslandName;
    }

    public Time getUnloadDelay() {
        return unloadDelay;
    }
}
