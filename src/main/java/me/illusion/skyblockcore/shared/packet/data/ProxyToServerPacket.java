package me.illusion.skyblockcore.shared.packet.data;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;

@Getter
public class ProxyToServerPacket extends Packet {

    private final String originProxy;
    private final String targetServer;

    public ProxyToServerPacket(byte[] bytes) {
        super(bytes);

        originProxy = readString();
        targetServer = readString();
    }

    public ProxyToServerPacket(String proxyId, String targetServer) {
        super(PacketDirection.PROXY_TO_INSTANCE);

        this.originProxy = proxyId;
        this.targetServer = targetServer;

        writeString(originProxy);
        writeString(targetServer);
    }
}
