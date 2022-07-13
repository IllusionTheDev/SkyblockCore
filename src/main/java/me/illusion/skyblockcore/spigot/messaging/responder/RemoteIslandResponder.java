package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRegisterRemoteIsland;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.island.Island;

import java.util.UUID;

public class RemoteIslandResponder implements PacketHandler<PacketRegisterRemoteIsland> {

    private final SkyblockPlugin main;

    public RemoteIslandResponder(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketRegisterRemoteIsland packet) {
        UUID islandId = packet.getIslandId();
        IslandData islandData = packet.getIslandData();

        Island island = main.getIslandManager().getIsland(islandId);

        if (island != null)
            return;

        main.getIslandManager().loadRemoteIsland(islandData);
    }
}
