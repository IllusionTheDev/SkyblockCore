package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

import java.util.UUID;

@Getter
public class PacketRequestIslandServer extends ProxyToServerPacket {

    private final UUID islandId;

    public PacketRequestIslandServer(byte[] bytes) {
        super(bytes);
        islandId = readUUID();
    }

    public PacketRequestIslandServer(String proxyId, String targetServer, UUID islandId) {
        super(proxyId, targetServer);
        this.islandId = islandId;
        writeUUID(islandId);
    }
}
