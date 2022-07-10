package me.illusion.skyblockcore.shared.packet;


import me.illusion.skyblockcore.shared.packet.data.PacketDirection;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * This is a packet manager implementation
 * made for cases where communication isn't set-up
 * properly, or disabled completely.
 */
public class DummyPacketManager extends PacketManager {

    public DummyPacketManager(String serverIdentifier) {
        super(serverIdentifier);
    }

    @Override
    public CompletableFuture<Void> send(Packet packet) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public List<PacketProcessor> getProcessors(PacketDirection direction) {
        return Collections.emptyList();
    }

    @Override
    public void registerProcessor(PacketDirection direction, PacketProcessor processor) {

    }

    @Override
    public Packet read(byte[] bytes) {
        return null;
    }

    @Override
    public <T extends Packet> T await(Class<T> packetClass, Predicate<T> returnIf, int timeoutSeconds) {
        return null;
    }

    @Override
    public <T extends Packet> T await(Class<T> clazz, Predicate<T> predicate) {
        return null;
    }
}
