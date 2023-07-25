package me.illusion.skyblockcore.spigot.network.simple;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.network.simple.command.SimpleIslandCommand;
import me.illusion.skyblockcore.spigot.network.simple.config.SimpleNetworkConfiguration;
import me.illusion.skyblockcore.spigot.network.simple.listener.SimplePlayerJoinListener;
import me.illusion.skyblockcore.spigot.network.simple.listener.SimplePlayerQuitListener;
import me.illusion.skyblockcore.spigot.network.simple.profile.SimpleProfileCache;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * This is a "simple" skyblock network, which is targeted at a single-instance network setup. The simple network will load islands when the player joins,
 * without doing any instance checks, and unload when the player quits.
 */
public class SimpleSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockSpigotPlugin plugin;

    private SimpleNetworkConfiguration configuration;

    public SimpleSkyblockNetwork(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        configuration = new SimpleNetworkConfiguration(plugin);

        registerProfileCache();
        registerListeners();
        registerCommands();
    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return "simple";
    }

    // Main startup logic

    private void registerProfileCache() {
        SimpleProfileCache profileCache = new SimpleProfileCache(plugin);

        registerListener(profileCache);
        plugin.setProfileCache(profileCache);
    }

    private void registerListeners() {
        registerListener(new SimplePlayerJoinListener(this));
        registerListener(new SimplePlayerQuitListener(this));
    }

    private void registerCommands() {
        registerCommand(new SimpleIslandCommand(this));
    }

    // Utility stuff

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private void registerCommand(AdvancedCommand command) {
        plugin.getCommandManager().registerCommand(command);
    }

    public SkyblockSpigotPlugin getPlugin() {
        return plugin;
    }

    public IslandManager getIslandManager() {
        return plugin.getIslandManager();
    }

    public MessagesFile getMessages() {
        return plugin.getMessages();
    }

    public SimpleNetworkConfiguration getConfiguration() {
        return configuration;
    }
}
