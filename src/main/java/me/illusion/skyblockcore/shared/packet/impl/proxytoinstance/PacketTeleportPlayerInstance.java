package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;

import java.util.UUID;

@Getter
public class PacketTeleportPlayerInstance extends ProxyToServerPacket {

    private final UUID playerId;
    private final SerializedLocation location;

    public PacketTeleportPlayerInstance(byte[] bytes) {
        super(bytes);

        playerId = readUUID();
        location = (SerializedLocation) readObject();
    }

    public PacketTeleportPlayerInstance(String targetServer, UUID playerId, SerializedLocation location) {
        super(targetServer);

        this.playerId = playerId;
        this.location = location;
    }

    @Override
    public void write() {
        writeUUID(playerId);
        writeObject(location);
    }
}
