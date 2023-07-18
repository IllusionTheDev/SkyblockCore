package me.illusion.skyblockcore.spigot.network.complex;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.network.complex.communication.CommunicationsHandler;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexIslandLoadListener;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexIslandUnloadListener;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexPlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public class ComplexSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockSpigotPlugin plugin;

    private SkyblockDatabase database;
    private CommunicationsHandler communicationsHandler;

    public ComplexSkyblockNetwork(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void enable(ConfigurationSection section) {

    }

    @Override
    public void disable() {
        communicationsHandler.disable();
    }

    @Override
    public String getName() {
        return "complex";
    }


    // Main startup logic

    private void registerListeners() {
        registerListener(new ComplexPlayerJoinListener(this));
        registerListener(new ComplexIslandLoadListener(this));
        registerListener(new ComplexIslandUnloadListener(this));
    }

    private void registerCommands() {

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

    public CommunicationsHandler getCommunicationsHandler() {
        return communicationsHandler;
    }

    public SkyblockDatabase getDatabase() {
        return database;
    }
}
