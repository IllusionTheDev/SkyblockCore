package me.illusion.skyblockcore.shared.packet.impl.proxy.proxy.response;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ProxyToProxyPacket;

import java.util.UUID;

@Getter
public class PacketRespondServer extends ProxyToProxyPacket {

    private final UUID uuid;
    private final String proxyId;
    private final String resultServer;

    public PacketRespondServer(byte[] bytes) {
        super(bytes);

        uuid = readUUID();
        proxyId = getOriginProxy();
        resultServer = readString();
    }

    public PacketRespondServer(UUID player, String proxyId, String targetProxy, String resultServer) {
        super(proxyId, targetProxy);

        this.uuid = player;
        this.proxyId = proxyId;
        this.resultServer = resultServer;

        writeUUID(uuid);
        writeString(proxyId);
        writeString(resultServer);
    }
}
