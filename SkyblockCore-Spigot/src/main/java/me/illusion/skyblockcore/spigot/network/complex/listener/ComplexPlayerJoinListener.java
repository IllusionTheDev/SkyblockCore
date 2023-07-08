package me.illusion.skyblockcore.spigot.network.complex.listener;

import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

        network.getDatabase().fetchIslandId(player.getUniqueId()).thenCompose(islandId -> network.getCommunicationsHandler().canLoad(islandId))
            .thenAccept(allowed -> {
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
