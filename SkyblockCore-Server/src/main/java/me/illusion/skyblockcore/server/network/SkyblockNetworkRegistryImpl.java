package me.illusion.skyblockcore.server.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;

/**
 * The skyblock network registry is responsible for loading the correct skyblock network structure. It is expected that separate plugins hook into this registry
 * and register their own skyblock network structures on startup. The network is loaded after all plugins enable.
 */
public class SkyblockNetworkRegistryImpl implements SkyblockNetworkRegistry {

    private final Map<String, SkyblockNetworkStructure> structures = new ConcurrentHashMap<>();

    private final SkyblockServerPlatform platform;
    private final ConfigurationSection config;

    private String desiredStructure;
    private boolean loaded = false;

    public SkyblockNetworkRegistryImpl(SkyblockServerPlatform platform) {
        this.platform = platform;

        this.config = platform.getConfigurationProvider().loadConfiguration("network-settings.yml");
    }

    /**
     * Registers a skyblock network structure.
     *
     * @param structure The structure to register.
     */
    @Override
    public void register(SkyblockNetworkStructure structure) {
        structures.put(structure.getName(), structure);
    }

    /**
     * Gets a skyblock network structure by name.
     *
     * @param name The name of the structure.
     * @return The structure, or null if it does not exist.
     */
    @Override
    public SkyblockNetworkStructure get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }

        return structures.get(name);
    }

    /**
     * Gets the network structure currently in use
     *
     * @return The active structure.
     */
    @Override
    public SkyblockNetworkStructure getActiveStructure() {
        if (desiredStructure == null) {
            throw new IllegalStateException("Network structure not loaded! Call this method after SkyblockEnableEvent is called.");
        }

        return structures.get(desiredStructure);
    }

    /**
     * Loads the skyblock network structure, as specified in the configuration file. If the structure does not exist, the plugin will be disabled.
     */
    @Override
    public void load() {
        if (desiredStructure != null) {
            throw new IllegalStateException("Network structure already initialized!");
        }

        String desired = config.getString("network-type", "undefined");

        SkyblockNetworkStructure structure = structures.get(desired);

        if (structure == null) {
            failToEnable(desired);
            return;
        }

        this.desiredStructure = desired;
        structure.load();
    }

    /**
     * Called when the plugin is done enabling and fully operational, including the database.
     */
    @Override
    public void enable() {
        if (loaded) {
            throw new IllegalStateException("Network structure already enabled!");
        }

        loaded = true;
        SkyblockNetworkStructure structure = getActiveStructure();
        structure.enable();
    }

    /**
     * Disables the plugin, as the network structure failed to enable, and the plugin cannot function without it.
     *
     * @param name The name of the network structure that failed to enable. This is used for logging purposes.
     */
    protected void failToEnable(String name) {
        Logger logger = platform.getLogger();

        logger.log(Level.SEVERE, "Failed to enable network structure {0}!", name);
        logger.severe("Please check your configuration file and try again.");
        logger.severe("Disabling plugin...");

        platform.disableExceptionally();
    }

}
