package me.illusion.skyblockcore.spigot.network.complex.listener;

import me.illusion.skyblockcore.spigot.event.island.SkyblockIslandLoadEvent;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This class is responsible for letting the communications handler know when an island has been loaded, so all other servers are aware of this island's
 * existence and current server id.
 */
public class ComplexIslandLoadListener implements Listener {

    private final ComplexSkyblockNetwork network;

    public ComplexIslandLoadListener(ComplexSkyblockNetwork network) {
        this.network = network;
    }

    @EventHandler
    private void onIslandLoad(SkyblockIslandLoadEvent event) {
        network.getCommunicationsHandler().updateIslandServer(event.getIsland());
    }
}
