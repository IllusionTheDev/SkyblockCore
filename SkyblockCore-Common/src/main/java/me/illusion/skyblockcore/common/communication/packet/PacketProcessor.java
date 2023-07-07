package me.illusion.skyblockcore.common.communication.packet;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface PacketProcessor {

    CompletableFuture<Void> send(Packet packet);

    void addCallback(Consumer<byte[]> receivedPacket);


}
