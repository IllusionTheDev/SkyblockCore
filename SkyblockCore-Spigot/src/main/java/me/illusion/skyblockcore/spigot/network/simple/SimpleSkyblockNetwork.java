package me.illusion.skyblockcore.spigot.network.simple;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.network.simple.command.SimpleIslandCommand;
import me.illusion.skyblockcore.spigot.network.simple.listener.SimplePlayerJoinListener;
import me.illusion.skyblockcore.spigot.network.simple.listener.SimplePlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public class SimpleSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockSpigotPlugin plugin;

    public SimpleSkyblockNetwork(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable(ConfigurationSection section) {
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
}
