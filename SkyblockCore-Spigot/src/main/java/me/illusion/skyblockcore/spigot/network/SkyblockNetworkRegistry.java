package me.illusion.skyblockcore.spigot.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class SkyblockNetworkRegistry {

    private final Map<String, SkyblockNetworkStructure> structures = new ConcurrentHashMap<>();

    private final SkyblockSpigotPlugin plugin;
    private final FileConfiguration config;

    private String desiredStructure;

    public SkyblockNetworkRegistry(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
        this.config = new YMLBase(plugin, "network-settings.yml").getConfiguration();
    }

    public void register(SkyblockNetworkStructure structure) {
        structures.put(structure.getName(), structure);
    }

    public SkyblockNetworkStructure get(String name) {
        return structures.get(name);
    }

    public SkyblockNetworkStructure getActiveStructure() {
        return structures.get(desiredStructure);
    }

    public void load() {
        String desiredStructure = config.getString("network-type", "undefined");

        SkyblockNetworkStructure structure = structures.get(desiredStructure);

        if (structure == null) {
            failToEnable(desiredStructure);
            return;
        }

        this.desiredStructure = desiredStructure;
        structure.enable(config.getConfigurationSection(desiredStructure));
    }

    public void failToEnable(String name) {
        Logger logger = plugin.getLogger();

        logger.severe("Failed to enable network structure " + name + "!");
        logger.severe("Please check your configuration file and try again.");
        logger.severe("Disabling plugin...");

        Bukkit.getPluginManager().disablePlugin(plugin);
    }

}
