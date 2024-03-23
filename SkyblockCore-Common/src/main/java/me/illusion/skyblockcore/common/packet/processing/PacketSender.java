package me.illusion.skyblockcore.common.packet.processing;

public interface PacketSender {

    void sendPacket(String targetId, byte[] packet);

    void subscribe(IncomingPacketListener listener);

    void subscribe(String channelId);

}
