package me.illusion.skyblockcore.spigot.network.complex.listener;

import me.illusion.skyblockcore.spigot.event.island.SkyblockIslandLoadEvent;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This class is responsible for letting the communications handler know when an island has been unloaded, so all other servers are aware this island is no
 * longer loaded.
 */
public class ComplexIslandUnloadListener implements Listener {

    private final ComplexSkyblockNetwork network;

    public ComplexIslandUnloadListener(ComplexSkyblockNetwork network) {
        this.network = network;
    }

    @EventHandler
    private void onIslandLoad(SkyblockIslandLoadEvent event) {
        network.getCommunicationsHandler().removeIsland(event.getIsland().getIslandId());
    }
}
