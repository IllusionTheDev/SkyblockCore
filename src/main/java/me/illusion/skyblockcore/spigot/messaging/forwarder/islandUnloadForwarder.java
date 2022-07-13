package me.illusion.skyblockcore.spigot.messaging.forwarder;

import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketUnregisterLoadedIsland;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandUnloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class islandUnloadForwarder implements Listener {

    private final SkyblockPlugin main;

    public islandUnloadForwarder(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onUnload(IslandUnloadEvent event) {
        UUID islandId = event.getIsland().getData().getId();

        PacketUnregisterLoadedIsland packet = new PacketUnregisterLoadedIsland(islandId);
        main.getPacketManager().send(packet);
    }
}
