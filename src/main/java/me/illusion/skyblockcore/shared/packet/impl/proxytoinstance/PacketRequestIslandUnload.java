package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

import java.util.UUID;

@Getter
public class PacketRequestIslandUnload extends ProxyToServerPacket {

    private final UUID islandId;

    public PacketRequestIslandUnload(byte[] bytes) {
        super(bytes);

        islandId = readUUID();
    }

    public PacketRequestIslandUnload(UUID islandId) {
        super((String) null);
        this.islandId = islandId;

        write();
    }

    @Override
    public void write() {
        writeUUID(islandId);
    }
}
