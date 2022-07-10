package me.illusion.skyblockcore.shared.packet.impl.proxytoproxy;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.data.ProxyToProxyPacket;

@Getter
public class PacketRedirectPacket extends ProxyToProxyPacket {

    private final byte[] bytes;

    public PacketRedirectPacket(String targetProxy, Packet packet) {
        super(targetProxy);

        this.bytes = packet.getAllBytes();

        write();
    }

    public PacketRedirectPacket(byte[] bytes) {
        super(bytes);

        this.bytes = readByteArray();
    }

    @Override
    public void write() {
        writeByteArray(bytes);
    }


}
