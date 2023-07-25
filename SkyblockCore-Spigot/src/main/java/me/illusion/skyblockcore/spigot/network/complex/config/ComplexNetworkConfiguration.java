package me.illusion.skyblockcore.spigot.network.complex.config;

import me.illusion.cosmos.utilities.storage.YMLBase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is responsible for loading the configuration for the complex skyblock network. It is expected that the configuration file is present in the
 * plugin's data folder.
 */
public class ComplexNetworkConfiguration extends YMLBase {

    private final String serverId;

    public ComplexNetworkConfiguration(JavaPlugin plugin) {
        super(plugin, "network/complex-network.yml");

        FileConfiguration configuration = getConfiguration();

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
