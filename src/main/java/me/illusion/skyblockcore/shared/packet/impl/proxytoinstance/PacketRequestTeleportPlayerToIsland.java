package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

import java.util.UUID;

@Getter
public class PacketRequestTeleportPlayerToIsland extends ProxyToServerPacket {

    private final UUID playerId;
    private final IslandData originalPlayerData;
    private final UUID islandId;

    public PacketRequestTeleportPlayerToIsland(byte[] bytes) {
        super(bytes);

        playerId = readUUID();
        originalPlayerData = (IslandData) readObject();
        islandId = readUUID();
    }

    public PacketRequestTeleportPlayerToIsland(String targetServer, UUID playerId, IslandData originalPlayerData, UUID islandId) {
        super(targetServer);

        this.playerId = playerId;
        this.originalPlayerData = originalPlayerData;
        this.islandId = islandId;

        write();
    }

    @Override
    public void write() {
        writeUUID(playerId);
        writeObject(originalPlayerData);
        writeUUID(islandId);
    }
}
