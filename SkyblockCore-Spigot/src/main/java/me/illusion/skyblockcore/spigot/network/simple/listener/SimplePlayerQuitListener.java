package me.illusion.skyblockcore.spigot.network.simple.listener;

import java.util.UUID;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.spigot.event.player.SkyblockPlayerQuitEvent;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This is the simple player quit listener, which unloads the island when the player quits.
 */
public class SimplePlayerQuitListener implements Listener {

    private final SimpleSkyblockNetwork network;

    public SimplePlayerQuitListener(SimpleSkyblockNetwork network) {
        this.network = network;
    }

    @EventHandler
    private void onQuit(SkyblockPlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID profileId = event.getProfileId();

        if (profileId == null) {
            return;
        }

        SkyblockIslandManager skyblockIslandManager = network.getPlugin().getIslandManager();

        SkyblockIsland island = skyblockIslandManager.getProfileIsland(profileId);

        if (island == null) {
            return;
        }

        // Unload the island after a while, this request will be cancelled if we attempt to load the island again, so no worries.
        skyblockIslandManager.requestUnloadIsland(island.getIslandId(), true, network.getConfiguration().getUnloadDelay());

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()); // Just to be sure
    }

}
