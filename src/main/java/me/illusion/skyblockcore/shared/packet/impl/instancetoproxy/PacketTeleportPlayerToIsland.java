package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

@Getter
public class PacketTeleportPlayerToIsland extends ServerToProxyPacket {

    private final UUID playerId;
    private final IslandData originalPlayerData;
    private final UUID islandId;

    public PacketTeleportPlayerToIsland(byte[] bytes) {
        super(bytes);

        playerId = readUUID();
        originalPlayerData = (IslandData) readObject();
        islandId = readUUID();
    }

    @Override
    public void write() {
        writeUUID(playerId);
        writeObject(originalPlayerData);
        writeUUID(islandId);
    }

    public PacketTeleportPlayerToIsland(UUID playerId, IslandData originalPlayerData, UUID islandId) {
        super();

        this.playerId = playerId;
        this.originalPlayerData = originalPlayerData;

        this.islandId = islandId;

        write();
    }
}
