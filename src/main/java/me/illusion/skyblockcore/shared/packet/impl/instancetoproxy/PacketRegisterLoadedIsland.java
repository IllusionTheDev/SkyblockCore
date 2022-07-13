package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

@Getter
public class PacketRegisterLoadedIsland extends ServerToProxyPacket {

    private final UUID islandId;
    private final IslandData islandData;

    public PacketRegisterLoadedIsland(byte[] bytes) {
        super(bytes);

        islandId = readUUID();
        islandData = (IslandData) readObject();
    }

    public PacketRegisterLoadedIsland(UUID islandId, IslandData islandData) {
        this.islandId = islandId;
        this.islandData = islandData;

        write();
    }

    @Override
    public void write() {
        writeUUID(islandId);
        writeObject(islandData);
    }
}
