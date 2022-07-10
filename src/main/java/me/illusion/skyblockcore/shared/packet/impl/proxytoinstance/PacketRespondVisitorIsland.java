package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

import java.util.UUID;

@Getter
public class PacketRespondVisitorIsland extends ProxyToServerPacket {

    private final String matchedServer;
    private final UUID requestId;

    public PacketRespondVisitorIsland(String targetServer, String matchedServer, UUID requestId) {
        super(targetServer);

        this.matchedServer = matchedServer;
        this.requestId = requestId;

        write();
    }

    public PacketRespondVisitorIsland(byte[] bytes) {
        super(bytes);

        this.matchedServer = readString();
        this.requestId = readUUID();
    }

    @Override
    public void write() {
        writeString(matchedServer);
        writeUUID(requestId);
    }

}
