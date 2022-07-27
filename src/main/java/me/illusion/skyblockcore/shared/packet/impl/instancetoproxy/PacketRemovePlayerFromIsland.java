package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

public class PacketRemovePlayerFromIsland extends ServerToProxyPacket {

    private final UUID playerId;
    private final UUID islandId;

    public PacketRemovePlayerFromIsland(byte[] bytes) {
        super(bytes);

        playerId = readUUID();
        islandId = readUUID();
    }

    public PacketRemovePlayerFromIsland(UUID playerId, UUID islandId) {
        super();

        this.playerId = playerId;
        this.islandId = islandId;

        write();
    }

    @Override
    public void write() {
        writeUUID(playerId);
        writeUUID(islandId);
    }
}
