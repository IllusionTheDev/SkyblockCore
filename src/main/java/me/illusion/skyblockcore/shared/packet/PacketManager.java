package me.illusion.skyblockcore.shared.packet;

import me.illusion.skyblockcore.shared.impl.proxy.proxy.request.PacketRequestServer;
import me.illusion.skyblockcore.shared.impl.proxy.proxy.response.PacketRespondServer;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketManager {

    private final Map<Byte, Class<? extends Packet>> identifiers = new HashMap<>();
    private final Map<PacketDirection, List<PacketProcessor>> processors = new HashMap<>();

    private void registerIds() {
        identifiers.put((byte) 0x01, PacketRequestServer.class);
        identifiers.put((byte) 0x02, PacketRespondServer.class);
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
    }

    public List<PacketProcessor> getProcessors(PacketDirection direction) {
        return processors.getOrDefault(direction, new ArrayList<>());
    }

    public Packet read(byte[] bytes) {
        Class<? extends Packet> type = getPacketClass(bytes[0]);

        try {
            return type.getConstructor(byte[].class).newInstance(bytes);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}
