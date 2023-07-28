package me.illusion.skyblockcore.spigot.network.complex.listener;

import java.util.UUID;
import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.spigot.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This class is responsible for loading the player's island when they join the server. If another instance is responsible for the island, we do not load it.
 * Feel free to fork or modify this code so you can handle rejection, if island visitation is not a feature.
 */
public class ComplexPlayerJoinListener implements Listener {

    private final ComplexSkyblockNetwork network;

    public ComplexPlayerJoinListener(ComplexSkyblockNetwork network) {
        this.network = network;
    }

    @EventHandler
    private void onJoin(SkyblockPlayerJoinEvent event) {
        Player player = event.getPlayer();

        SkyblockIsland cached = network.getIslandManager().getProfileIsland(event.getProfileId());

        if (cached != null) {
            player.teleport(SkyblockBukkitAdapter.toBukkitLocation(cached.getCenter()));
            return;
        }

        // We try to fetch the island id, and see if we can load it. If we can, we load it.
        network.getDatabase()
            .fetchIslandId(event.getProfileId()) // Fetch the island id
            .thenCompose(islandId -> network.getCommunicationsHandler().canLoad(islandId)) // Check if we can load the island
            .thenAccept(allowed -> { // If we can load the island, we load it.
                if (allowed) {
                    tryLoadDefault(event.getProfileId(), player);
                }
            });
    }

    private void tryLoadDefault(UUID profileId, Player player) {
        network.getIslandManager().loadPlayerIsland(profileId, "default").thenAcceptAsync(island -> {

            player.teleport(SkyblockBukkitAdapter.toBukkitLocation(island.getCenter()));

        }, MainThreadExecutor.INSTANCE);
    }
}
