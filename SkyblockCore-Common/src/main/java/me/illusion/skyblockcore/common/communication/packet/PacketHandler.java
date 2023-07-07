package me.illusion.skyblockcore.common.communication.packet;

public interface PacketHandler<T extends Packet> {

    default void onSend(T packet) {
    }

    default void onReceive(T packet) {
    }
}
