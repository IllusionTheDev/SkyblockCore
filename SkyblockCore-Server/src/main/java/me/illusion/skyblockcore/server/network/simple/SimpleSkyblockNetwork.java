package me.illusion.skyblockcore.server.network.simple;

import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.server.network.simple.config.SimpleNetworkConfiguration;
import me.illusion.skyblockcore.server.network.simple.listener.SimplePlayerJoinListener;
import me.illusion.skyblockcore.server.network.simple.listener.SimplePlayerQuitListener;

/**
 * This is a "simple" skyblock network, which is targeted at a single-instance network setup. The simple network will load islands when the player joins,
 * without doing any instance checks, and unload when the player quits.
 */
public class SimpleSkyblockNetwork implements SkyblockNetworkStructure {

    private final SkyblockServerPlatform platform;

    private SimpleNetworkConfiguration configuration;

    public SimpleSkyblockNetwork(SkyblockServerPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void enable() {
        configuration = new SimpleNetworkConfiguration(platform);

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
        new SimplePlayerJoinListener(this);
        new SimplePlayerQuitListener(this);
    }

    private void registerCommands() {
        // registerCommand(new SimpleIslandCommand(this));
    }

    // Utility stuff

    /*
    private void registerCommand(AdvancedCommand command) {
        plugin.getCommandManager().registerCommand(command);
    }

     */

    public SkyblockServerPlatform getPlatform() {
        return platform;
    }

    public SkyblockIslandManager getIslandManager() {
        return platform.getIslandManager();
    }

    /*
    public MessagesFile getMessages() {
        return plugin.getMessages();
    }

     */

    public SimpleNetworkConfiguration getConfiguration() {
        return configuration;
    }

    public SkyblockEventManager getEventManager() {
        return platform.getEventManager();
    }
}
