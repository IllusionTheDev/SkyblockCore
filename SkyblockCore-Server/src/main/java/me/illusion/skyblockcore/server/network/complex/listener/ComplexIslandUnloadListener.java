package me.illusion.skyblockcore.server.network.complex.listener;

import me.illusion.skyblockcore.server.event.island.SkyblockIslandLoadEvent;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;

/**
 * This class is responsible for letting the communications handler know when an island has been unloaded, so all other servers are aware this island is no
 * longer loaded.
 */
public class ComplexIslandUnloadListener {

    private final ComplexSkyblockNetwork network;

    public ComplexIslandUnloadListener(ComplexSkyblockNetwork network) {
        this.network = network;

        network.getEventManager().subscribe(SkyblockIslandLoadEvent.class, this::handle);
    }

    private void handle(SkyblockIslandLoadEvent event) {
        network.getCommunicationsHandler().removeIsland(event.getIsland().getIslandId());
    }
}
