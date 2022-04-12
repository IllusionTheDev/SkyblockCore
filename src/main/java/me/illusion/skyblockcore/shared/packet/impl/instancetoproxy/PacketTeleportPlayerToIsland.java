package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

@Getter
public class PacketTeleportPlayerToIsland extends ServerToProxyPacket {

    private final UUID playerId;
    private final UUID islandId;

    public PacketTeleportPlayerToIsland(byte[] bytes) {
        super(bytes);

        playerId = readUUID();
        islandId = readUUID();
    }

    public PacketTeleportPlayerToIsland(UUID playerId, UUID islandId) {
        super();

        this.playerId = playerId;
        this.islandId = islandId;

        writeUUID(playerId);
        writeUUID(islandId);
    }
}
