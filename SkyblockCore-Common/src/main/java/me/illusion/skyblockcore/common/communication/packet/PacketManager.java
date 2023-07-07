package me.illusion.skyblockcore.common.communication.packet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class PacketManager {


    private static final Map<Byte, Class<? extends Packet>> identifiers = new HashMap<>();
    private final List<PacketProcessor> processors = new ArrayList<>();
    private final Map<Byte, List<PacketHandler<Packet>>> handlers = new HashMap<>();

    private final Cache<UUID, Boolean> ignoredPackets = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.SECONDS)
        .build();

    private final PacketWaiter waiter;

    public PacketManager() {
        waiter = new PacketWaiter(this);


    }

    public static void registerPacket(int packetId, Class<? extends Packet> packetClass) {
        registerPacket((byte) packetId, packetClass);
    }

    public static void registerPacket(byte packetId, Class<? extends Packet> packetClass) {
        if (identifiers.containsKey(packetId)) {
            throw new UnsupportedOperationException(
                "Packet identifier for packet " + packetClass.getSimpleName()
                    + " is already registered. ");
        }

        identifiers.put(packetId, packetClass);
    }


    public static byte getIdentifier(Class<? extends Packet> clazz) {
        for (Map.Entry<Byte, Class<? extends Packet>> entry : identifiers.entrySet()) {
            if (clazz.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        // If it isn't registered, just register it.
        registerPacket(identifiers.size(), clazz);

        return getIdentifier(clazz);
    }

    public Class<? extends Packet> getPacketClass(byte identifier) {
        return identifiers.get(identifier);
    }

    public void registerProcessor(PacketProcessor processor) {
        processors.add(processor);
        processor.addCallback(this::read);
    }

    public CompletableFuture<Void> send(Packet packet) {

        ignoredPackets.put(packet.getPacketId(), true);

        Set<CompletableFuture<Void>> futures = new HashSet<>();

        try {
            for (PacketProcessor processor : processors) {
                futures.add(processor.send(packet));
            }

            byte id = packet.getIdentifier();
            List<PacketHandler<Packet>> handler = handlers.get(id);

            if (handler == null) {
                System.out.println("No handlers for packet " + packet.getClass().getSimpleName() + " " + id);
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            }

            for (PacketHandler<Packet> packetHandler : handler) {
                packetHandler.onSend(packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public Packet read(byte[] bytes) {
        Class<? extends Packet> type = getPacketClass(bytes[0]);

        if (type == null) {
            return null;
        }

        try {
            Packet packet = type.getConstructor(byte[].class).newInstance(bytes);

            UUID packetId = packet.getPacketId();

            if (ignoredPackets.getIfPresent(packetId) != null) {
                ignoredPackets.invalidate(packetId);
                return null;
            }

            List<PacketHandler<Packet>> handler = handlers.get(bytes[0]);

            if (handler != null) {
                for (PacketHandler<Packet> packetHandler : handler) {
                    packetHandler.onReceive(packet);
                }
            }

            return packet;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T extends Packet> void subscribe(Class<T> packetClass, PacketHandler<T> handler) {
        byte identifier = getIdentifier(packetClass);

        handlers.putIfAbsent(identifier, new ArrayList<>());
        handlers.get(identifier).add((PacketHandler<Packet>) handler);
    }

    public <T extends Packet> CompletableFuture<T> await(Class<T> clazz, Predicate<T> predicate) {
        return waiter.await(clazz, predicate);
    }
}
