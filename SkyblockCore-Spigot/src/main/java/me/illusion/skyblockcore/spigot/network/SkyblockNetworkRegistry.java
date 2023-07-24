package me.illusion.skyblockcore.spigot.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The skyblock network registry is responsible for loading the correct skyblock network structure. It is expected that separate plugins hook into this registry
 * and register their own skyblock network structures on startup. The network is loaded after all plugins enable.
 */
public class SkyblockNetworkRegistry {

    private final Map<String, SkyblockNetworkStructure> structures = new ConcurrentHashMap<>();

    private final SkyblockSpigotPlugin plugin;
    private final FileConfiguration config;

    private String desiredStructure;

    public SkyblockNetworkRegistry(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
        this.config = new YMLBase(plugin, "network-settings.yml").getConfiguration();
    }

    /**
     * Registers a skyblock network structure.
     *
     * @param structure The structure to register.
     */
    public void register(SkyblockNetworkStructure structure) {
        structures.put(structure.getName(), structure);
    }

    /**
     * Gets a skyblock network structure by name.
     *
     * @param name The name of the structure.
     * @return The structure, or null if it does not exist.
     */
    public SkyblockNetworkStructure get(String name) {
        return structures.get(name);
    }

    /**
     * Gets the network structure currently in use
     *
     * @return The active structure.
     */
    public SkyblockNetworkStructure getActiveStructure() {
        return structures.get(desiredStructure);
    }

    /**
     * Loads the skyblock network structure, as specified in the configuration file. If the structure does not exist, the plugin will be disabled.
     */
    public void load() {
        String desiredStructure = config.getString("network-type", "undefined");

        SkyblockNetworkStructure structure = structures.get(desiredStructure);

        if (structure == null) {
            failToEnable(desiredStructure);
            return;
        }

        this.desiredStructure = desiredStructure;
        structure.load();
    }

    /**
     * Called when the plugin is done enabling and fully operational, including the database.
     */
    public void enable() {
        SkyblockNetworkStructure desiredStructure = getActiveStructure();
        desiredStructure.enable(config.getConfigurationSection(desiredStructure.getName()));
    }

    /**
     * Disables the plugin, as the network structure failed to enable, and the plugin cannot function without it.
     *
     * @param name The name of the network structure that failed to enable. This is used for logging purposes.
     */
    public void failToEnable(String name) {
        Logger logger = plugin.getLogger();

        logger.severe("Failed to enable network structure " + name + "!");
        logger.severe("Please check your configuration file and try again.");
        logger.severe("Disabling plugin...");

        Bukkit.getPluginManager().disablePlugin(plugin);
    }

}
