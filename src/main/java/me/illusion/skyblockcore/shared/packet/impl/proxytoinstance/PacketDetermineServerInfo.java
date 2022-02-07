package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

public class PacketDetermineServerInfo extends ProxyToServerPacket {
    public PacketDetermineServerInfo(byte[] bytes) {
        super(bytes);
    }

    public PacketDetermineServerInfo(String proxyId, String targetServer) {
        super(proxyId, targetServer);
    }
}
