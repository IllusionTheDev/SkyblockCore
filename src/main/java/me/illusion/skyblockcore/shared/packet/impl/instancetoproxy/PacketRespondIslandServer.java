package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.ServerInfo;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

import java.util.UUID;

@Getter
public class PacketRespondIslandServer extends ServerToProxyPacket {

    private final UUID islandId;

    private final boolean found;
    private final byte islandCount;
    private final byte islandCapacity;

    public PacketRespondIslandServer(byte[] bytes) {
        super(bytes);

        islandId = readUUID();

        found = readBoolean();
        islandCount = readByte();
        islandCapacity = readByte();
    }

    public PacketRespondIslandServer(String serverName, UUID islandId, boolean found, byte islandCount, byte islandCapacity) {
        super(serverName);
        this.islandId = islandId;
        this.found = found;
        this.islandCount = islandCount;
        this.islandCapacity = islandCapacity;

        writeUUID(islandId);
        writeBoolean(found);
        writeByte(islandCount);
        writeByte(islandCapacity);
    }

    public ServerInfo asServerInfo() {
        return new ServerInfo(islandCount, islandCapacity, getServerName());
    }
}
