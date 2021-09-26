package me.illusion.skyblockcore.shared.impl.proxy.proxy.request;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;

import java.util.UUID;

@Getter
public class PacketRequestServer extends Packet {

    private final UUID uuid;
    private final String proxyId;

    public PacketRequestServer(byte[] bytes) {
        super(bytes);

        this.uuid = readUUID();
        this.proxyId = readString();
    }

    public PacketRequestServer(UUID uuid, String proxyId) {
        super(PacketDirection.PROXY_TO_PROXY);

        this.uuid = uuid;
        this.proxyId = proxyId;

        writeUUID(uuid);
        writeString(proxyId);
    }
}
