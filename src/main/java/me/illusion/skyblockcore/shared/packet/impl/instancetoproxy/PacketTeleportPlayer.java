package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;

import java.util.UUID;

@Getter
public class PacketTeleportPlayer extends ServerToProxyPacket {

    private final UUID uuid;
    private final String targetServerId;
    private final SerializedLocation serializedLocation;

    public PacketTeleportPlayer(byte[] bytes) {
        super(bytes);

        this.uuid = this.readUUID();
        this.targetServerId = this.readString();
        this.serializedLocation = (SerializedLocation) this.readObject();
    }

    public PacketTeleportPlayer(UUID uuid, String targetServer, SerializedLocation serializedLocation) {
        super();

        this.uuid = uuid;
        this.targetServerId = targetServer;
        this.serializedLocation = serializedLocation;

        write();
    }

    @Override
    public void write() {
        this.writeUUID(uuid);
        this.writeString(targetServerId);
        this.writeObject(serializedLocation);
    }
}