package me.illusion.skyblockcore.shared.packet.impl.proxytoproxy.response;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.PacketManager;
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

    @Override
    public void write() {
        writeUUID(uuid);
        writeString(proxyId);
        writeString(resultServer);
    }

    public PacketRespondServer(UUID player, String targetProxy, String resultServer) {
        super(targetProxy);

        this.uuid = player;
        this.proxyId = PacketManager.getServerIdentifier();
        this.resultServer = resultServer;

        write();
    }
}
