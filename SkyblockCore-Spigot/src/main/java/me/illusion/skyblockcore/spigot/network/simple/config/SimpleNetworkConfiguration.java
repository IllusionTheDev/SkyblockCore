package me.illusion.skyblockcore.spigot.network.simple.config;

import lombok.Getter;
import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.cosmos.utilities.time.Time;
import me.illusion.skyblockcore.spigot.utilities.time.TimeParser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is responsible for loading the configuration for the simple skyblock network. It is expected that the configuration file is present in the
 * plugin's data folder.
 */
@Getter
public class SimpleNetworkConfiguration extends YMLBase {

    private final String defaultIslandName;
    private final Time unloadDelay;

    public SimpleNetworkConfiguration(JavaPlugin plugin) {
        super(plugin, "network/simple-network.yml");

        FileConfiguration configuration = getConfiguration();

        defaultIslandName = configuration.getString("island.default-name", "default");
        unloadDelay = parseTime("island.unload-delay");
    }

    private Time parseTime(String path) {
        return TimeParser.parse(getConfiguration().getString(path, "10 minutes"));
    }
}
