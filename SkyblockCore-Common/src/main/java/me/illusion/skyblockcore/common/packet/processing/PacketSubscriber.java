package me.illusion.skyblockcore.common.packet.processing;

import me.illusion.skyblockcore.common.packet.Packet;

public interface PacketSubscriber<T extends Packet> {

    void onReceive(String sourceId, T packet);

}
