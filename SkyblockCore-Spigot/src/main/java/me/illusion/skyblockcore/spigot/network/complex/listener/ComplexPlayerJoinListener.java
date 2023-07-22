package me.illusion.skyblockcore.spigot.network.complex.listener;

import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Island cached = network.getIslandManager().getPlayerIsland(player);

        if (cached != null) {
            player.teleport(cached.getCenter());
            return;
        }

        // We try to fetch the island id, and see if we can load it. If we can, we load it.
        network.getDatabase()
            .fetchIslandId(player.getUniqueId()) // Fetch the island id
            .thenCompose(islandId -> network.getCommunicationsHandler().canLoad(islandId)) // Check if we can load the island
            .thenAccept(allowed -> { // If we can load the island, we load it.
                if (allowed) {
                    tryLoadDefault(player);
                }
            });
    }

    private void tryLoadDefault(Player player) {
        network.getIslandManager().loadPlayerIsland(player, "default").thenAcceptAsync(island -> {

            player.teleport(island.getCenter());

        }, MainThreadExecutor.INSTANCE);
    }
}
