package me.illusion.skyblockcore.server.network.complex;

import lombok.Getter;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.server.network.complex.command.ComplexIslandCommand;
import me.illusion.skyblockcore.server.network.complex.communication.CommunicationsHandler;
import me.illusion.skyblockcore.server.network.complex.communication.listener.TeleportRequestPacketHandler;
import me.illusion.skyblockcore.server.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.server.network.complex.config.ComplexNetworkConfiguration;
import me.illusion.skyblockcore.server.network.complex.listener.ComplexIslandLoadListener;
import me.illusion.skyblockcore.server.network.complex.listener.ComplexIslandUnloadListener;
import me.illusion.skyblockcore.server.network.complex.listener.ComplexPlayerJoinListener;

/**
 * Represents a complex SkyblockNetworkStructure. A complex structure is one that has multiple instances, where each instance claims ownership over a group of
 * islands, and handles all the communication belonging to that group.
 */
@Getter
public class ComplexSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockServerPlatform platform;

    private SkyblockFetchingDatabase database;
    private CommunicationsHandler communicationsHandler;

    private ComplexNetworkConfiguration configuration;

    public ComplexSkyblockNetwork(SkyblockServerPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void load() {
        platform.getDatabasesFile().setSupportsFileBased(false); // We don't support file-based databases, as they are instance-specific
    }

    @Override
    public void enable() {
        database = platform.getDatabaseRegistry().getChosenDatabase();
        configuration = new ComplexNetworkConfiguration(platform);

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
        new ComplexIslandCommand(this);
    }

    // Utility stuff

    public SkyblockIslandManager getIslandManager() {
        return platform.getIslandManager();
    }

    public SkyblockCacheDatabase getCacheDatabase() {
        return platform.getDatabaseRegistry().getChosenCacheDatabase();
    }

    public SkyblockEventManager getEventManager() {
        return platform.getEventManager();
    }

    public SkyblockMessagesFile getMessages() {
        return platform.getMessagesFile();
    }
}
