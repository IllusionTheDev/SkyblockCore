package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketUnregisterRemoteIsland;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

import java.util.UUID;

public class UnloadRemoteIslandResponder implements PacketHandler<PacketUnregisterRemoteIsland> {

    private final SkyblockPlugin main;

    public UnloadRemoteIslandResponder(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketUnregisterRemoteIsland packet) {
        UUID islandId = packet.getIslandId();

        main.getIslandManager().unregisterRemoteIsland(islandId);
    }
}
