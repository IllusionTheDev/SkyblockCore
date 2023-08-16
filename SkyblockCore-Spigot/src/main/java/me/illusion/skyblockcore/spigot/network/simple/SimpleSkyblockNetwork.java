package me.illusion.skyblockcore.spigot.network.simple;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.network.simple.command.SimpleIslandCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * This is a "simple" skyblock network, which is targeted at a single-instance network setup. The simple network will load islands when the player joins,
 * without doing any instance checks, and unload when the player quits.
 */
public class SimpleSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockSpigotPlugin plugin;

    public SimpleSkyblockNetwork(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        registerCommands();
    }

    @Override
    public void disable() {
        // There's no unloading logic for the simple network, as it's not needed.
    }

    @Override
    public String getName() {
        return "simple";
    }

    // Main startup logic

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

    public SkyblockIslandManager getIslandManager() {
        return plugin.getIslandManager();
    }

    public MessagesFile getMessages() {
        return plugin.getMessages();
    }

    public SkyblockEventManager getEventManager() {
        return plugin.getEventManager();
    }
}
