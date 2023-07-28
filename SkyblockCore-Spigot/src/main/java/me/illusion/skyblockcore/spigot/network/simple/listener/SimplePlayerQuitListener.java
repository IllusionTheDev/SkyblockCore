package me.illusion.skyblockcore.spigot.network.simple.listener;

import java.util.UUID;
import me.illusion.skyblockcore.server.event.player.SkyblockPlayerQuitEvent;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This is the simple player quit listener, which unloads the island when the player quits.
 */
public class SimplePlayerQuitListener {

    private final SimpleSkyblockNetwork network;
    private final SkyblockIslandManager islandManager;

    public SimplePlayerQuitListener(SimpleSkyblockNetwork network) {
        this.network = network;
        this.islandManager = network.getPlugin().getIslandManager();

        network.getEventManager().subscribe(SkyblockPlayerQuitEvent.class, this::handle);
    }

    private void handle(SkyblockPlayerQuitEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerId());
        UUID profileId = event.getProfileId();

        if (player == null) {
            return;
        }

        if (profileId == null) {
            return;
        }

        SkyblockIsland island = islandManager.getProfileIsland(profileId);

        if (island == null) {
            return;
        }

        // Unload the island after a while, this request will be cancelled if we attempt to load the island again, so no worries.
        islandManager.requestUnloadIsland(island.getIslandId(), true, network.getConfiguration().getUnloadDelay());
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()); // Just to be sure
    }

}
