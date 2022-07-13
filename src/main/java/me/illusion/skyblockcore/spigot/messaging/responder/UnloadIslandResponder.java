package me.illusion.skyblockcore.spigot.messaging.responder;

import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestIslandUnload;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

import java.util.UUID;

public class UnloadIslandResponder implements PacketHandler<PacketRequestIslandUnload> {

    private final SkyblockPlugin main;

    public UnloadIslandResponder(SkyblockPlugin main) {
        this.main = main;
    }

    @Override
    public void onReceive(PacketRequestIslandUnload packet) {
        UUID islandId = packet.getIslandId();

        main.getIslandManager().deleteIsland(islandId);
    }
}
