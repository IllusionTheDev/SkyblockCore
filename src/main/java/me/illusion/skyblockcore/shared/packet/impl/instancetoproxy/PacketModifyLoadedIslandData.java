package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

@Getter
public class PacketModifyLoadedIslandData extends ServerToProxyPacket {

    private final UUID islandId;
    private final IslandData newData;

    public PacketModifyLoadedIslandData(byte[] bytes) {
        super(bytes);

        islandId = readUUID();
        newData = (IslandData) readObject();
    }

    public PacketModifyLoadedIslandData(UUID islandId, IslandData newData) {
        super();

        this.islandId = islandId;
        this.newData = newData;

        write();
    }

    @Override
    public void write() {
        writeUUID(islandId);
        writeObject(newData);
    }
}
