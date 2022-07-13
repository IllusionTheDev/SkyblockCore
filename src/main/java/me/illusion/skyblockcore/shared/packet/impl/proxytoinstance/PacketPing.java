package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

public class PacketPing extends ProxyToServerPacket {


    public PacketPing(byte[] bytes) {
        super(bytes);
    }

    public PacketPing() {
        super((String) null);
    }

    @Override
    public void write() {

    }
}
