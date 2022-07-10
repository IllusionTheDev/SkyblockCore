package me.illusion.skyblockcore.shared.packet.data;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketManager;

@Getter
public abstract class ProxyToProxyPacket extends Packet {

    private final String originProxy;
    private final String targetProxy;

    public ProxyToProxyPacket(byte[] bytes) {
        super(bytes);

        originProxy = readString();
        targetProxy = readString();
    }

    public ProxyToProxyPacket(String targetProxy) {
        super(PacketDirection.PROXY_TO_PROXY);

        this.originProxy = PacketManager.getServerIdentifier();
        this.targetProxy = targetProxy;

        writeString(originProxy);
        writeString(targetProxy);
    }
}
