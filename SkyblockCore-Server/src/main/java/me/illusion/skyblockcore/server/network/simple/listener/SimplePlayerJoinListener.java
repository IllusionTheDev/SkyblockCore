package me.illusion.skyblockcore.server.network.simple.listener;

import java.util.UUID;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.simple.SimpleSkyblockNetwork;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * This is the simple player join listener, which loads the island when the player joins.
 */
public class SimplePlayerJoinListener {

    private final SimpleSkyblockNetwork network;
    private final SkyblockIslandManager islandManager;

    public SimplePlayerJoinListener(SimpleSkyblockNetwork network) {
        this.network = network;
        this.islandManager = network.getPlatform().getIslandManager();

        network.getEventManager().subscribe(SkyblockPlayerJoinEvent.class, this::handle);
    }

    private void handle(SkyblockPlayerJoinEvent event) {
        SkyblockPlayer player = event.getPlayer();
        UUID profileId = player.getUniqueId();

        String defaultIslandName = network.getConfiguration().getDefaultIslandName();

        islandManager.loadPlayerIsland(profileId, defaultIslandName).thenAccept(island -> {

            player.teleport(island.getCenter());

        });
    }

}
