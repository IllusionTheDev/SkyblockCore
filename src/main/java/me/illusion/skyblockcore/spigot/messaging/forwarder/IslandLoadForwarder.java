package me.illusion.skyblockcore.spigot.messaging.forwarder;

import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRegisterLoadedIsland;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.event.IslandLoadEvent;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.island.impl.LoadedIsland;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IslandLoadForwarder implements Listener {

    private final SkyblockPlugin main;

    public IslandLoadForwarder(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onislandLoad(IslandLoadEvent event) {
        Island island = event.getIsland();

        if (!(island instanceof LoadedIsland))
            return;

        PacketRegisterLoadedIsland packet = new PacketRegisterLoadedIsland(island.getData().getId(), island.getData());
        main.getPacketManager().send(packet);
    }
}
