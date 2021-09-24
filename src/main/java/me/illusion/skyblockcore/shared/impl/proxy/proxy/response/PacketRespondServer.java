package me.illusion.skyblockcore.shared.impl.proxy.proxy.response;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;

import java.util.UUID;

@Getter
public class PacketRespondServer extends Packet {

    private final UUID uuid;
    private final String proxyId;
    private final String resultServer;

    public PacketRespondServer(byte[] bytes) {
        super(bytes);

        uuid = readUUID();
        proxyId = readString();
        resultServer = readString();
    }

    public PacketRespondServer(byte identifier, UUID player, String proxyId, String resultServer) {
        super(identifier, PacketDirection.PROXY_TO_PROXY);

        this.uuid = player;
        this.proxyId = proxyId;
        this.resultServer = resultServer;

        writeUUID(uuid);
        writeString(proxyId);
        writeString(resultServer);
    }
}
