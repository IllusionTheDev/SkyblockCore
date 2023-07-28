package me.illusion.skyblockcore.server.network;

/**
 * This is the skyblock network registry, which is used to register skyblock networks.
 */
public interface SkyblockNetworkRegistry {

    /**
     * Register a skyblock network structure.
     *
     * @param structure The structure to register.
     */
    void register(SkyblockNetworkStructure structure);

    /**
     * Get a skyblock network structure by name.
     *
     * @param name The name of the structure.
     * @return The structure, or null if not found.
     */
    SkyblockNetworkStructure get(String name);

    /**
     * Get the active skyblock network structure.
     *
     * @return The active structure.
     */
    SkyblockNetworkStructure getActiveStructure();

    /**
     * Load the skyblock network registry, initializing the active structure.
     */
    void load();

    /**
     * Enable the skyblock network registry, enabling the active structure.
     */
    void enable();
}
