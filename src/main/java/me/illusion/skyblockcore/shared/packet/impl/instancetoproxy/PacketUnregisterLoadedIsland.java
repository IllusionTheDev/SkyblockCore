package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

@Getter
public class PacketUnregisterLoadedIsland extends ServerToProxyPacket {

    private final UUID islandId;

    public PacketUnregisterLoadedIsland(byte[] bytes) {
        super(bytes);

        islandId = readUUID();
    }

    public PacketUnregisterLoadedIsland(UUID islandId) {
        super();

        this.islandId = islandId;

        write();
    }

    @Override
    public void write() {
        writeUUID(islandId);
    }
}
