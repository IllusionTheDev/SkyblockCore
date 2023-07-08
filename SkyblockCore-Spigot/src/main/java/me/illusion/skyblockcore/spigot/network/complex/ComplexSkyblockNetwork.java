package me.illusion.skyblockcore.spigot.network.complex;

import me.illusion.cosmos.utilities.command.command.impl.AdvancedCommand;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkStructure;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public class ComplexSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockSpigotPlugin plugin;

    private PacketManager packetManager;

    public ComplexSkyblockNetwork(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void enable(ConfigurationSection section) {
        initPackets();
    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return "complex";
    }

    private void initPackets() {
        packetManager = new PacketManager();
    }

    // Main startup logic

    private void registerListeners() {

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

    public PacketManager getPacketManager() {
        return packetManager;
    }
}
