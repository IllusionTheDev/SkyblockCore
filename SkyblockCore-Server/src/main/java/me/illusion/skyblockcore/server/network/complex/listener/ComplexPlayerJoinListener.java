package me.illusion.skyblockcore.server.network.complex.listener;

import java.util.UUID;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * This class is responsible for loading the player's island when they join the server. If another instance is responsible for the island, we do not load it.
 * Feel free to fork or modify this code so you can handle rejection, if island visitation is not a feature.
 */
public class ComplexPlayerJoinListener {

    private final ComplexSkyblockNetwork network;
    private final SkyblockIslandManager islandManager;

    public ComplexPlayerJoinListener(ComplexSkyblockNetwork network) {
        this.network = network;
        this.islandManager = network.getIslandManager();

        network.getEventManager().subscribe(SkyblockPlayerJoinEvent.class, this::handle);
    }

    private void handle(SkyblockPlayerJoinEvent event) {
        SkyblockPlayer player = event.getPlayer();
        UUID profileId = player.getSelectedProfileId();

        SkyblockIsland cached = islandManager.getProfileIsland(profileId);

        if (cached != null) {
            player.teleport(cached.getCenter());
            return;
        }

        // We try to fetch the island id, and see if we can load it. If we can, we load it.
        network.getDatabase()
            .getIslandId(profileId) // Fetch the island id
            .thenCompose(islandId -> network.getCommunicationsHandler().canLoad(islandId)) // Check if we can load the island
            .thenAccept(allowed -> { // If we can load the island, we load it.
                if (Boolean.TRUE.equals(allowed)) { // CF returns a boxed boolean, let's prevent null pointer exceptions.
                    tryLoadDefault(profileId, player);
                }
            });
    }

    private void tryLoadDefault(UUID profileId, SkyblockPlayer player) {
        islandManager.loadPlayerIsland(profileId, "default").thenAccept(island -> player.teleport(island.getCenter()));
    }
}
