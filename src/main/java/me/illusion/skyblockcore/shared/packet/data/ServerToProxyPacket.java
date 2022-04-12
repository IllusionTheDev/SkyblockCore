package me.illusion.skyblockcore.shared.packet.data;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketManager;

@Getter
public class ServerToProxyPacket extends Packet {

    private final String serverName;

    public ServerToProxyPacket(byte[] bytes) {
        super(bytes);

        serverName = readString();
    }

    public ServerToProxyPacket() {
        super(PacketDirection.INSTANCE_TO_PROXY);

        this.serverName = PacketManager.getServerIdentifier();
        writeString(serverName);


    }
}
