package me.illusion.skyblockcore.shared.packet.impl.proxy.instance.response;

import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

public class PacketDetermineProxy extends ProxyToServerPacket {

    public PacketDetermineProxy(byte[] bytes) {
        super(bytes);
    }

    public PacketDetermineProxy(String proxyId, String targetServer) {
        super(proxyId, targetServer);
    }
}
