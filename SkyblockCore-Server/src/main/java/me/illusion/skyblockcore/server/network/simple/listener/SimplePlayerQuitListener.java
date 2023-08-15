package me.illusion.skyblockcore.server.network.simple.listener;

import java.util.UUID;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerQuitEvent;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.simple.SimpleSkyblockNetwork;

/**
 * This is the simple player quit listener, which unloads the island when the player quits.
 */
public class SimplePlayerQuitListener {

    private final SimpleSkyblockNetwork network;
    private final SkyblockIslandManager islandManager;

    public SimplePlayerQuitListener(SimpleSkyblockNetwork network) {
        this.network = network;
        this.islandManager = network.getPlatform().getIslandManager();

        network.getEventManager().subscribe(SkyblockPlayerQuitEvent.class, this::handle);
    }

    private void handle(SkyblockPlayerQuitEvent event) {
        UUID profileId = event.getPlayer().getSelectedProfileId();

        if (profileId == null) {
            return;
        }

        SkyblockIsland island = islandManager.getProfileIsland(profileId);

        if (island == null) {
            return;
        }

        // Unload the island after a while, this request will be cancelled if we attempt to load the island again, so no worries.
        islandManager.requestUnloadIsland(island.getIslandId(), true, network.getConfiguration().getUnloadDelay());
    }

}
