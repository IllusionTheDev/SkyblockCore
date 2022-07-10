package me.illusion.skyblockcore.shared.packet.data;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketManager;

@Getter
public abstract class ProxyToServerPacket extends Packet {

    private final String originProxy;
    private final String targetServer;

    public ProxyToServerPacket(byte[] bytes) {
        super(bytes);

        originProxy = readString();
        String targetServer = readString();

        if (targetServer.equalsIgnoreCase("null"))
            targetServer = null;

        this.targetServer = targetServer;
    }

    public ProxyToServerPacket(String targetServer) {
        super(PacketDirection.PROXY_TO_INSTANCE);

        this.originProxy = PacketManager.getServerIdentifier();
        this.targetServer = targetServer;

        writeString(originProxy);

        if (targetServer == null)
            writeString("null");
        else
            writeString(targetServer);
    }
}
