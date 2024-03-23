package me.illusion.skyblockcore.common.packet.processing;

import java.util.LinkedList;
import java.util.Queue;
import me.illusion.skyblockcore.common.packet.Packet;

public class PacketSubscriptionQueue<T extends Packet> {

    private final Class<T> packetClass;
    private final Queue<PacketSubscriber<T>> subscribers;

    public PacketSubscriptionQueue(Class<T> packetClass) {
        this.packetClass = packetClass;
        this.subscribers = new LinkedList<>();
    }

    public void subscribe(PacketSubscriber<?> incomingPacketListener) {
        subscribers.add((PacketSubscriber<T>) incomingPacketListener);
    }

    public void onReceive(String sourceId, Packet packet) {
        subscribers.forEach(subscriber -> subscriber.onReceive(sourceId, packetClass.cast(packet)));
    }

    public Class<T> getPacketClass() {
        return packetClass;
    }
}
