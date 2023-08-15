package me.illusion.skyblockcore.spigot.network.complex;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.network.complex.command.ComplexIslandCommand;
import me.illusion.skyblockcore.spigot.network.complex.communication.CommunicationsHandler;
import me.illusion.skyblockcore.spigot.network.complex.communication.listener.TeleportRequestPacketHandler;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.spigot.network.complex.config.ComplexNetworkConfiguration;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexIslandLoadListener;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexIslandUnloadListener;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexPlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Represents a complex SkyblockNetworkStructure. A complex structure is one that has multiple instances, where each instance claims ownership over a group of
 * islands, and handles all the communication belonging to that group.
 */
public class ComplexSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockSpigotPlugin plugin;

    private SkyblockFetchingDatabase database;
    private CommunicationsHandler communicationsHandler;

    private ComplexNetworkConfiguration configuration;

    public ComplexSkyblockNetwork(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        plugin.getDatabasesFile().setSupportsFileBased(false); // We don't support file-based databases, as they are instance-specific
    }

    @Override
    public void enable() {
        database = plugin.getDatabaseRegistry().getChosenDatabase();
        configuration = new ComplexNetworkConfiguration(plugin);

        registerListeners();
        registerCommands();
        registerPacketHandlers();
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
        new ComplexPlayerJoinListener(this);
        new ComplexIslandLoadListener(this);
        new ComplexIslandUnloadListener(this);
    }

    private void registerPacketHandlers() {
        PacketManager packetManager = communicationsHandler.getPacketManager();

        packetManager.subscribe(PacketRequestIslandTeleport.class, new TeleportRequestPacketHandler(this));
    }

    private void registerCommands() {
        registerCommand(new ComplexIslandCommand(this));
    }

    // Utility stuff

    public void registerListener(Listener listener) {
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

    public CommunicationsHandler getCommunicationsHandler() {
        return communicationsHandler;
    }

    public SkyblockFetchingDatabase getDatabase() {
        return database;
    }

    public SkyblockCacheDatabase getCacheDatabase() {
        return plugin.getDatabaseRegistry().getChosenCacheDatabase();
    }

    public ComplexNetworkConfiguration getConfiguration() {
        return configuration;
    }

    public SkyblockEventManager getEventManager() {
        return plugin.getEventManager();
    }
}
