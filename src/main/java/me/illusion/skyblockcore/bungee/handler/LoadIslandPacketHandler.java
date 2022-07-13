package me.illusion.skyblockcore.bungee.handler;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRegisterLoadedIsland;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRegisterRemoteIsland;

import java.util.UUID;

public class LoadIslandPacketHandler implements PacketHandler<PacketRegisterLoadedIsland> {

    private final SkyblockBungeePlugin main;

    public LoadIslandPacketHandler(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketRegisterLoadedIsland packet) {
        UUID IslandId = packet.getIslandId();
        IslandData islandData = packet.getIslandData();

        PacketRegisterRemoteIsland response = new PacketRegisterRemoteIsland(IslandId, islandData);
        main.getPacketManager().send(response);
    }
}
