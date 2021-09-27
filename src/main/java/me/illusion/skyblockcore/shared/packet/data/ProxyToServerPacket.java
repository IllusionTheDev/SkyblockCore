package me.illusion.skyblockcore.shared.packet.data;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;

@Getter
public class ProxyToServerPacket extends Packet {

    private final String targetServer;

    public ProxyToServerPacket(byte[] bytes) {
        super(bytes);

        targetServer = readString();
    }

    public ProxyToServerPacket(String targetServer) {
        super(PacketDirection.PROXY_TO_INSTANCE);

        this.targetServer = targetServer;

        writeString(targetServer);
    }
}
