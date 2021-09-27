package me.illusion.skyblockcore.shared.packet;

import me.illusion.skyblockcore.shared.impl.proxy.proxy.request.PacketRequestMessageSend;
import me.illusion.skyblockcore.shared.impl.proxy.proxy.response.PacketRespondServer;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketManager {

    private static final Map<Byte, Class<? extends Packet>> identifiers = new HashMap<>();
    private final Map<PacketDirection, List<PacketProcessor>> processors = new HashMap<>();
    private final Map<Byte, List<PacketHandler<Packet>>> handlers = new HashMap<>();


    public PacketManager() {
        registerIds();
    }

    public static void registerPacket(int packetId, Class<? extends Packet> packetClass) {
        registerPacket((byte) packetId, packetClass);
    }

    public static void registerPacket(byte packetId, Class<? extends Packet> packetClass) {
        if (identifiers.containsKey(packetId))
            throw new UnsupportedOperationException("Packet identifier for packet " + packetClass.getSimpleName() + " is already registered. ");

        identifiers.put(packetId, packetClass);
    }

    private void registerIds() {
        registerPacket(0x01, PacketRespondServer.class);
        registerPacket(0x02, PacketRespondServer.class);
        registerPacket(0x03, PacketRequestMessageSend.class);

    }

    public static byte getIdentifier(Class<? extends Packet> clazz) {
        for (Map.Entry<Byte, Class<? extends Packet>> entry : identifiers.entrySet()) {
            if (clazz.equals(entry.getValue()))
                return entry.getKey();
        }

        return 0;
    }

    public Class<? extends Packet> getPacketClass(byte identifier) {
        return identifiers.get(identifier);
    }

    public void registerProcessor(PacketDirection direction, PacketProcessor processor) {
        List<PacketProcessor> list = processors.getOrDefault(direction, null);

        if (list == null) {
            list = new ArrayList<>();
            processors.put(direction, list);
        }

        list.add(processor);
    }

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

    public List<PacketProcessor> getProcessors(PacketDirection direction) {
        return processors.getOrDefault(direction, new ArrayList<>());
    }

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

    public <T extends Packet> void subscribe(Class<T> packetClass, PacketHandler<T> handler) {
        byte identifier = getIdentifier(packetClass);

        handlers.putIfAbsent(identifier, new ArrayList<>());
        handlers.get(identifier).add((PacketHandler<Packet>) handler);
    }
}
