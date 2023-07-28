package me.illusion.skyblockcore.server.network;

/**
 * The network structure system acts as a sort of "plugin" for the SkyblockCore ecosystem. Its main purpose is to split all the possible network types into
 * different groups, so that simple single-instanced networks have simple code running, and we end up with less, simpler code, or in the case of complex
 * networks, we do all the complex logic.
 */
public interface SkyblockNetworkStructure {

    /**
     * Called before any database loads. This is where you tell the database setup if you like files or not.
     */
    default void load() {
    }

    /**
     * Called when the network is enabled. This is where you should register all your listeners, commands, etc.
     */
    void enable();

    /**
     * Called when the network is disabled. This is where you should unregister all your listeners, commands, etc.
     */
    void disable();

    /**
     * Gets the name of this network type. This is used to identify the network type in the configuration file.
     *
     * @return The name of this network type.
     */
    String getName();
}
