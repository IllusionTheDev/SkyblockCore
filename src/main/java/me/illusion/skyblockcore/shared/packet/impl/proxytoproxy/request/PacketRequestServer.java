package me.illusion.skyblockcore.shared.packet.impl.proxytoproxy.request;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ProxyToProxyPacket;

import java.util.UUID;

@Getter
public class PacketRequestServer extends ProxyToProxyPacket {

    private final UUID uuid;

    public PacketRequestServer(byte[] bytes) {
        super(bytes);

        this.uuid = readUUID();
    }

    public PacketRequestServer(UUID uuid, String proxyId, String targetProxy) {
        super(proxyId, targetProxy);

        this.uuid = uuid;

        writeUUID(uuid);
    }
}
