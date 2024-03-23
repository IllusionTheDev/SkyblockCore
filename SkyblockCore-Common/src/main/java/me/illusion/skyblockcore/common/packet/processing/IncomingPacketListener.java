package me.illusion.skyblockcore.common.packet.processing;

@FunctionalInterface
public interface IncomingPacketListener {

    void onReceive(String sourceId, byte[] data);

}
