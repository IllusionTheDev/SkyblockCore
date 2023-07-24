package me.illusion.skyblockcore.spigot.network.complex;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.common.database.structure.SkyblockDatabase;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.network.complex.command.ComplexIslandCommand;
import me.illusion.skyblockcore.spigot.network.complex.communication.CommunicationsHandler;
import me.illusion.skyblockcore.spigot.network.complex.communication.listener.TeleportRequestPacketHandler;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexIslandLoadListener;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexIslandUnloadListener;
import me.illusion.skyblockcore.spigot.network.complex.listener.ComplexPlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

/**
 * Represents a complex SkyblockNetworkStructure. A complex structure is one that has multiple instances, where each instance claims ownership over a group of
 * islands, and handles all the communication belonging to that group.
 */
public class ComplexSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockSpigotPlugin plugin;

    private SkyblockDatabase database;
    private CommunicationsHandler communicationsHandler;

    public ComplexSkyblockNetwork(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        plugin.getDatabasesFile().setSupportsFileBased(false); // We don't support file-based databases, as they are instance-specific
    }

    @Override
    public void enable(ConfigurationSection section) {
        database = plugin.getDatabase();

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
        registerListener(new ComplexPlayerJoinListener(this));
        registerListener(new ComplexIslandLoadListener(this));
        registerListener(new ComplexIslandUnloadListener(this));
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
