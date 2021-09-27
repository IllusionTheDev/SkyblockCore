package me.illusion.skyblockcore.shared.packet.data;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;

@Getter
public class ProxyToProxyPacket extends Packet {

    private final String originProxy;
    private final String targetProxy;

    public ProxyToProxyPacket(byte[] bytes) {
        super(bytes);

        originProxy = readString();
        targetProxy = readString();
    }

    public ProxyToProxyPacket(String originProxy, String targetProxy) {
        super(PacketDirection.PROXY_TO_PROXY);

        this.originProxy = originProxy;
        this.targetProxy = targetProxy;

        writeString(originProxy);
        writeString(targetProxy);
    }
}
