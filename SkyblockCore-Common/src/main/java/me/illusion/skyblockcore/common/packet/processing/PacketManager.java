package me.illusion.skyblockcore.common.packet.processing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.packet.Packet;
import me.illusion.skyblockcore.common.packet.channel.PacketChannel;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteInputStream;
import me.illusion.skyblockcore.common.packet.stream.FriendlyByteOutputStream;
import me.illusion.skyblockcore.common.utilities.reflection.Reflect;
import me.illusion.skyblockcore.common.utilities.reflection.ReflectedConstructor;
import me.illusion.skyblockcore.common.utilities.reflection.ReflectedConstructorCache;

public class PacketManager implements IncomingPacketListener {

    private final ReflectedConstructorCache constructorCache = Reflect.aConstructorCache();

    private final PacketSender sender;
    private final Map<Class<? extends Packet>, PacketSubscriptionQueue<?>> subscriptionQueues;
    private final String serverId;

    public PacketManager(String serverId, PacketSender sender) {
        this.subscriptionQueues = new ConcurrentHashMap<>();
        this.serverId = serverId;
        this.sender = sender;

        this.sender.subscribe(this);
    }

    public void subscribeChannel(PacketChannel channel) {
        for (String c : channel.getChannels()) {
            sender.subscribe(c);
        }
    }

    public <T extends Packet> void subscribe(Class<T> packetClass, PacketSubscriber<T> subscriber) {
        PacketSubscriptionQueue<?> queue = subscriptionQueues.computeIfAbsent(packetClass, PacketSubscriptionQueue::new);
        queue.subscribe(subscriber);
    }

    public void sendPacket(PacketChannel target, Packet packet) {
        FriendlyByteOutputStream outputStream = new FriendlyByteOutputStream();

        outputStream.writeString(packet.getClass().getName());
        outputStream.writeString(this.serverId);
        packet.writeData(outputStream);

        byte[] data = outputStream.toByteArray();

        for (String channel : target.getChannels()) {
            sender.sendPacket(channel, data);
        }

        // System.out.println("Sent packet " + packet.getClass().getName() + " to " + target);
    }

    @Override
    public void onReceive(String channel, byte[] data) {
        FriendlyByteInputStream inputStream = new FriendlyByteInputStream(data);
        String packetClassName = inputStream.readString();
        String sourceId = inputStream.readString();

        if (sourceId.equals(this.serverId)) {
            return; // Ignore packets from this server
        }

        ReflectedConstructor<?> constructor = constructorCache.getConstructor(packetClassName);

        if (constructor == null) {
            System.out.println("Received packet " + packetClassName + " from " + sourceId + " but it could not be constructed");
            return;
        }

        Packet packet = (Packet) constructor.newInstance();
        packet.readData(inputStream);

        // System.out.println("Received packet " + packet.getClass().getName() + " from " + sourceId);

        PacketSubscriptionQueue<?> queue = subscriptionQueues.get(packet.getClass());

        if (queue != null) {
            queue.onReceive(sourceId, packet);
        }

    }
}
