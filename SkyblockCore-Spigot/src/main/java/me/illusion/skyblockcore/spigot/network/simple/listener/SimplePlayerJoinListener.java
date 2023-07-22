package me.illusion.skyblockcore.spigot.network.simple.listener;

import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This is the simple player join listener, which loads the island when the player joins.
 */
public class SimplePlayerJoinListener implements Listener {

    private final SimpleSkyblockNetwork network;

    public SimplePlayerJoinListener(SimpleSkyblockNetwork network) {
        this.network = network;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        network.getPlugin().getIslandManager().loadPlayerIsland(player, network.getConfiguration().getDefaultIslandName()).thenAcceptAsync(island -> {

            player.teleport(island.getCenter());

        }, MainThreadExecutor.INSTANCE); // Need to use main thread executor due to async teleportation not being allowed
    }

}
