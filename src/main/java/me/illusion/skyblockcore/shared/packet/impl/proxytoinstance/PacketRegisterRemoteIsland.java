package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

import java.util.UUID;

@Getter
public class PacketRegisterRemoteIsland extends ProxyToServerPacket {

    private final UUID islandId;
    private final IslandData islandData;

    public PacketRegisterRemoteIsland(byte[] bytes) {
        super(bytes);

        islandId = readUUID();
        islandData = (IslandData) readObject();
    }

    public PacketRegisterRemoteIsland(UUID islandId, IslandData islandData) {
        super((String) null); // send to all servers

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
