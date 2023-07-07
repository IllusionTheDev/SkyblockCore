package me.illusion.skyblockcore.common.communication.packet.processor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import me.illusion.skyblockcore.common.communication.packet.Packet;
import me.illusion.skyblockcore.common.communication.packet.PacketProcessor;
import me.illusion.skyblockcore.common.communication.redis.RedisController;
import redis.clients.jedis.BinaryJedisPubSub;

public class RedisProcessor extends BinaryJedisPubSub implements PacketProcessor {

    private final byte[] channelBytes;
    private final RedisController controller;

    private Consumer<byte[]> callback = bytes -> {
    };

    public RedisProcessor(String channel, RedisController controller) {
        this.controller = controller;

        this.channelBytes = channel.getBytes(StandardCharsets.UTF_8);

        new Thread(() -> {
            controller.getJedis().subscribe(this, channelBytes);
        }).start();
    }

    @Override
    public CompletableFuture<Void> send(Packet packet) {
        return controller.borrow(jedis -> {
            byte[] bytes = packet.getAllBytes();
            jedis.publish(channelBytes, bytes);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @Override
    public void addCallback(Consumer<byte[]> receivedPacket) {
        callback = callback.andThen(receivedPacket);
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        if (!Arrays.equals(channel, channelBytes)) {
            return;
        }

        callback.accept(message);
    }
}
