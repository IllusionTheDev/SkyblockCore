package me.illusion.skyblockcore.spigot.network;

import me.illusion.skyblockcore.server.network.AbstractSkyblockNetworkRegistry;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import org.bukkit.Bukkit;

public class SkyblockNetworkRegistryImpl extends AbstractSkyblockNetworkRegistry {

    private final SkyblockSpigotPlugin plugin;

    public SkyblockNetworkRegistryImpl(SkyblockSpigotPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected void failToEnable(String name) {
        super.failToEnable(name);

        Bukkit.getPluginManager().disablePlugin(plugin);
    }
}
