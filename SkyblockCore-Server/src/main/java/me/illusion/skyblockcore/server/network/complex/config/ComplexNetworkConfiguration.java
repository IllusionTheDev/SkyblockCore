package me.illusion.skyblockcore.server.network.complex.config;

import lombok.Getter;
import me.illusion.skyblockcore.common.config.AbstractConfiguration;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * This class is responsible for loading the configuration for the complex skyblock network. It is expected that the configuration file is present in the
 * plugin's data folder.
 */
@Getter
public class ComplexNetworkConfiguration extends AbstractConfiguration {

    /**
     * -- GETTER -- Gets the server id of this server. This should match the id in your proxy, and is used to identify this server in all communication
     * efforts.
     *
     * @return The server id.
     */
    private final String serverId;

    public ComplexNetworkConfiguration(SkyblockPlatform platform) {
        super(platform, "network/complex-network.yml");

        serverId = configuration.getString("server-id", "skyblock1");
    }

}
