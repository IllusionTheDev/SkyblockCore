package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;
import me.illusion.skyblockcore.shared.sql.serialized.SerializedLocation;

import java.util.UUID;

@Getter
public class PacketTeleportPlayer extends ServerToProxyPacket {

    private final UUID uuid;
    private final SerializedLocation serializedLocation;

    public PacketTeleportPlayer(byte[] bytes) {
        super(bytes);

        this.uuid = this.readUUID();
        this.serializedLocation = (SerializedLocation) this.readObject();
    }

    public PacketTeleportPlayer(UUID uuid, SerializedLocation serializedLocation) {
        super();

        this.uuid = uuid;
        this.serializedLocation = serializedLocation;

        this.writeUUID(uuid);
        this.writeObject(serializedLocation);
    }
}
