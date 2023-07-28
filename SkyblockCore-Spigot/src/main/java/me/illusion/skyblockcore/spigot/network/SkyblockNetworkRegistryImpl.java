package me.illusion.skyblockcore.spigot.network;

import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.server.network.AbstractSkyblockNetworkRegistry;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.utilities.config.BukkitConfigurationAdapter;
import org.bukkit.Bukkit;

public class SkyblockNetworkRegistryImpl extends AbstractSkyblockNetworkRegistry {

    private final SkyblockSpigotPlugin plugin;

    public SkyblockNetworkRegistryImpl(SkyblockSpigotPlugin plugin) {
        super(plugin, BukkitConfigurationAdapter.adapt(new YMLBase(plugin, "network-settings.yml").getConfiguration()));
        this.plugin = plugin;
    }

    @Override
    protected void failToEnable(String name) {
        super.failToEnable(name);

        Bukkit.getPluginManager().disablePlugin(plugin);
    }
}
