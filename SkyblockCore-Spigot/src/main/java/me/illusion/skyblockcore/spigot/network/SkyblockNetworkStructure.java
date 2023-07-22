package me.illusion.skyblockcore.spigot.network;

import org.bukkit.configuration.ConfigurationSection;

/**
 * The network structure system acts as a sort of "plugin" for the SkyblockCore ecosystem. Its main purpose is to split all the possible network types into
 * different groups, so that simple single-instanced networks have simple code running, and we end up with less, simpler code, or in the case of complex
 * networks, we do all the complex logic.
 */
public interface SkyblockNetworkStructure {

    /**
     * Called when the network is enabled. This is where you should register all your listeners, commands, etc.
     *
     * @param section The configuration section for this network type. This is where you should get all your configuration values from.
     */
    void enable(ConfigurationSection section);

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
