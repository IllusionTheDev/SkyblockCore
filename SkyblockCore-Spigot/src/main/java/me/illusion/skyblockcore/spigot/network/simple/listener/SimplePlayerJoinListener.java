package me.illusion.skyblockcore.spigot.network.simple.listener;

import java.util.UUID;
import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerJoinEvent;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import me.illusion.skyblockcore.spigot.utilities.adapter.SkyblockBukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This is the simple player join listener, which loads the island when the player joins.
 */
public class SimplePlayerJoinListener {

    private final SimpleSkyblockNetwork network;
    private final SkyblockIslandManager islandManager;

    public SimplePlayerJoinListener(SimpleSkyblockNetwork network) {
        this.network = network;
        this.islandManager = network.getPlugin().getIslandManager();

        network.getEventManager().subscribe(SkyblockPlayerJoinEvent.class, this::handle);
    }

    private void handle(SkyblockPlayerJoinEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerId());

        if (player == null) {
            return;
        }

        UUID profileId = event.getProfileId();
        String defaultIslandName = network.getConfiguration().getDefaultIslandName();

        islandManager.loadPlayerIsland(profileId, defaultIslandName).thenAcceptAsync(island -> {

            player.teleport(SkyblockBukkitAdapter.toBukkitLocation(island.getCenter()));

        }, MainThreadExecutor.INSTANCE); // Need to use main thread executor due to async teleportation not being allowed
    }

}
