package me.illusion.skyblockcore.server.network.complex.config;

import me.illusion.skyblockcore.common.config.impl.AbstractDatabaseConfiguration;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * This class is responsible for loading the configuration for the complex skyblock network. It is expected that the configuration file is present in the
 * plugin's data folder.
 */
public class ComplexNetworkConfiguration extends AbstractDatabaseConfiguration {

    private final String serverId;

    public ComplexNetworkConfiguration(SkyblockPlatform platform) {
        super(platform, "network/complex-network.yml");

        serverId = configuration.getString("server-id", "skyblock1");
    }

    /**
     * Gets the server id of this server. This should match the id in your proxy, and is used to identify this server in all communication efforts.
     *
     * @return The server id.
     */
    public String getServerId() {
        return serverId;
    }
}
