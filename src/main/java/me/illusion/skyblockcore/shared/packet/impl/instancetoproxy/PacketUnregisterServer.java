package me.illusion.skyblockcore.shared.packet.impl.instancetoproxy;

import me.illusion.skyblockcore.shared.packet.data.ServerToProxyPacket;

public class PacketUnregisterServer extends ServerToProxyPacket {

    public PacketUnregisterServer(byte[] bytes) {
        super(bytes);
    }

    public PacketUnregisterServer() {
    }

    @Override
    public void write() {

    }
}
