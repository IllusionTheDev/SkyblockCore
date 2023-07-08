package me.illusion.skyblockcore.spigot.network;

import org.bukkit.configuration.ConfigurationSection;

public interface SkyblockNetworkStructure {

    void enable(ConfigurationSection section);

    void disable();

    String getName();
}
