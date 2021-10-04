package me.illusion.skyblockcore.shared.packet;

import me.illusion.skyblockcore.shared.packet.data.PacketDirection;
import me.illusion.skyblockcore.shared.packet.impl.proxy.proxy.request.PacketRequestMessageSend;
import me.illusion.skyblockcore.shared.packet.impl.proxy.proxy.response.PacketRespondServer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PacketManager {

    private static final Map<Byte, Class<? extends Packet>> identifiers = new HashMap<>();
    private final Map<PacketDirection, List<PacketProcessor>> processors = new HashMap<>();
    private final Map<Byte, List<PacketHandler<Packet>>> handlers = new HashMap<>();

    private final PacketWaiter waiter;

    public PacketManager() {
        registerIds();
        waiter = new PacketWaiter(this);
    }

    /**
     * Registers a packet
     *
     * @param packetId    - Packet ID (int form)
     * @param packetClass - Packet class
     */
    public static void registerPacket(int packetId, Class<? extends Packet> packetClass) {
        registerPacket((byte) packetId, packetClass);
    }

    /**
     * Registers a packet
     *
     * @param packetId    - Packet ID (byte form)
     * @param packetClass - Packet class
     */
    public static void registerPacket(byte packetId, Class<? extends Packet> packetClass) {
        if (identifiers.containsKey(packetId))
            throw new UnsupportedOperationException("Packet identifier for packet " + packetClass.getSimpleName() + " is already registered. ");

        identifiers.put(packetId, packetClass);
    }

    /**
     * Obtain a packet's identifier
     *
     * @param clazz - The packet class
     * @return 0 if the packet is not found, PACKET_ID otherwise
     */
    public static byte getIdentifier(Class<? extends Packet> clazz) {
        for (Map.Entry<Byte, Class<? extends Packet>> entry : identifiers.entrySet()) {
            if (clazz.equals(entry.getValue()))
                return entry.getKey();
        }

        return 0;
    }

    /**
     * Registers the built-in packets
     */
    private void registerIds() {
        registerPacket(0x01, PacketRespondServer.class);
        registerPacket(0x02, PacketRespondServer.class);
        registerPacket(0x03, PacketRequestMessageSend.class);
    }

    /**
     * Obtains a packet's class via its identifier
     *
     * @param identifier - The packet's identifier
     * @return packet class
     */
    public Class<? extends Packet> getPacketClass(byte identifier) {
        return identifiers.get(identifier);
    }

    /**
     * Registers a packet processor (sender)
     *
     * @param direction - The packet direction (PROXY_TO_INSTANCE, for example)
     * @param processor - The packet processor
     */
    public void registerProcessor(PacketDirection direction, PacketProcessor processor) {
        List<PacketProcessor> list = processors.getOrDefault(direction, null);

        if (list == null) {
            list = new ArrayList<>();
            processors.put(direction, list);
        }

        list.add(processor);
    }

    /**
     * Sends a packet, using all the appropriate processors
     *
     * @param packet - The packet to send
     */
    public void send(Packet packet) {
        PacketDirection direction = packet.getDirection();
        List<PacketProcessor> processors = getProcessors(direction);

        for (PacketProcessor processor : processors)
            processor.send(packet);

        byte id = packet.getIdentifier();
        List<PacketHandler<Packet>> handler = handlers.get(id);

        if (handler == null)
            return;

        for (PacketHandler<Packet> packetHandler : handler)
            packetHandler.onSend(packet);
    }

    /**
     * Obtains all processors for a specific PacketDirection
     *
     * @param direction - The packet direction
     * @return The list, shouldn't be empty unless you're tossing PROXY_TO_PROXY on an instance.
     */
    public List<PacketProcessor> getProcessors(PacketDirection direction) {
        return processors.getOrDefault(direction, new ArrayList<>());
    }

    /**
     * Reads a packet from a byte[], calling all handlers
     *
     * @param bytes - The serialized packet data
     * @return The packet instance, generally not needed.
     */
    public Packet read(byte[] bytes) {
        Class<? extends Packet> type = getPacketClass(bytes[0]);

        try {
            Packet packet = type.getConstructor(byte[].class).newInstance(bytes);
            List<PacketHandler<Packet>> handler = handlers.get(bytes[0]);

            if (handler != null)
                for (PacketHandler<Packet> packetHandler : handler)
                    packetHandler.onReceive(packet);

            return packet;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Registers a listener/handler for a specific packet class
     *
     * @param packetClass - The packet class
     * @param handler     - The packet handler
     * @param <T>         - The packet type
     */
    public synchronized <T extends Packet> void subscribe(Class<T> packetClass, PacketHandler<T> handler) {
        byte identifier = getIdentifier(packetClass);

        handlers.putIfAbsent(identifier, new ArrayList<>());
        handlers.get(identifier).add((PacketHandler<Packet>) handler);
    }

    /**
     * Awaits for a packet, should be called async
     *
     * @param clazz     - The packet's class
     * @param predicate - A filter operation
     * @param <T>       - Packet type
     * @return NULL if timed out (after 15 seconds), the packet otherwise
     */
    public <T extends Packet> T await(Class<T> clazz, Predicate<T> predicate) {
        return waiter.await(clazz, predicate);
    }

    /**
     * Awaits for a packet, should be called async
     *
     * @param packetClass    - The packet's class
     * @param returnIf       - A filter operation
     * @param timeoutSeconds - Seconds until a timeout is determined
     * @param <T>            - Packet Type
     * @return NULL if timeout, Packet otherwise
     */
    public <T extends Packet> T await(Class<T> packetClass, Predicate<T> returnIf, int timeoutSeconds) {
        return waiter.await(packetClass, returnIf, timeoutSeconds);
    }
}
