package me.illusion.skyblockcore.server.network.complex.listener;

import me.illusion.skyblockcore.server.event.island.SkyblockIslandLoadEvent;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;

/**
 * This class is responsible for letting the communications handler know when an island has been loaded, so all other servers are aware of this island's
 * existence and current server id.
 */
public class ComplexIslandLoadListener {

    private final ComplexSkyblockNetwork network;

    public ComplexIslandLoadListener(ComplexSkyblockNetwork network) {
        this.network = network;

        network.getEventManager().subscribe(SkyblockIslandLoadEvent.class, this::handle);
    }

    private void handle(SkyblockIslandLoadEvent event) {
        network.getCommunicationsHandler().updateIslandServer(event.getIsland());
    }
}
