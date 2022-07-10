package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

@Getter
public class PacketRequestVisitorIsland extends ServerToProxyPacket {

    private final UUID islandId;
    private final UUID packetId;

    public PacketRequestVisitorIsland(byte[] bytes) {
        super(bytes);

        islandId = readUUID();
        packetId = readUUID();
    }

    public PacketRequestVisitorIsland(UUID islandId) {
        super();

        this.islandId = islandId;
        this.packetId = UUID.randomUUID();

        write();

    }

    @Override
    public void write() {
        writeUUID(islandId);
        writeUUID(packetId);
    }

}
