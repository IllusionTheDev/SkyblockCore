package me.illusion.skyblockcore.common.communication.packet.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import me.illusion.skyblockcore.common.communication.packet.Packet;
import me.illusion.skyblockcore.common.communication.packet.PacketProcessor;
import me.illusion.skyblockcore.common.communication.redis.RedisController;
import me.illusion.skyblockcore.common.database.structure.SkyblockCacheDatabase;
import redis.clients.jedis.BinaryJedisPubSub;

public class RedisProcessor extends BinaryJedisPubSub implements PacketProcessor, SkyblockCacheDatabase {

    private final byte[] channelBytes;
    private final RedisController controller;

    private Consumer<byte[]> callback = bytes -> {
    };

    public RedisProcessor(String channel, RedisController controller) {
        this.controller = controller;

        this.channelBytes = channel.getBytes(StandardCharsets.UTF_8);

        new Thread(() -> {
            controller.getJedis().subscribe(this, channelBytes, "global".getBytes(StandardCharsets.UTF_8));
        }).start();
    }

    @Override
    public CompletableFuture<Void> send(String server, Packet packet) {
        return controller.borrow(jedis -> {
            byte[] bytes = packet.getAllBytes();
            jedis.publish(server.getBytes(StandardCharsets.UTF_8), bytes);
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
        if (!Arrays.equals(channel, channelBytes) && !Arrays.equals(channel, "global".getBytes(StandardCharsets.UTF_8))) {
            return;
        }

        callback.accept(message);
    }

    @Override
    public CompletableFuture<String> getIslandServer(UUID islandId) {
        return controller.supply(jedis -> jedis.hget("island-servers", islandId.toString()));
    }

    @Override
    public CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId) {
        return controller.borrow(jedis -> jedis.hset("island-servers", islandId.toString(), serverId));
    }

    @Override
    public CompletableFuture<Void> removeServer(String serverId) {
        return controller.borrow(jedis -> {
            jedis.hdel("island-servers", serverId);
        });
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return controller.borrow(jedis -> {
            jedis.hdel("island-servers", islandId.toString());
        });
    }

    private byte[] createKey(String message) {
        return (new String(channelBytes) + ":" + message).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] serialize(Serializable object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T extends Serializable> T deserialize(byte[] bytes) {
        try {
            return (T) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
