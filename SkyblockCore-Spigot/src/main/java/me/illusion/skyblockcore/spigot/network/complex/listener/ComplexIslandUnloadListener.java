package me.illusion.skyblockcore.spigot.network.complex.listener;

import me.illusion.skyblockcore.spigot.event.island.SkyblockIslandLoadEvent;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
