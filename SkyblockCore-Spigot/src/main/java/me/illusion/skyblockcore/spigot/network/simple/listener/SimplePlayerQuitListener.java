package me.illusion.skyblockcore.spigot.network.simple.listener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.illusion.cosmos.utilities.time.Time;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SimplePlayerQuitListener implements Listener {

    private final SimpleSkyblockNetwork network;

    public SimplePlayerQuitListener(SimpleSkyblockNetwork network) {
        this.network = network;
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        Island island = network.getPlugin().getIslandManager().getPlayerIsland(playerId);

        network.getPlugin().getIslandManager().requestUnloadIsland(island.getIslandId(), true, new Time(10, TimeUnit.MINUTES));

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()); // Just to be sure
    }

}
