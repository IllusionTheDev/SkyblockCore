package me.illusion.skyblockcore.spigot.messaging;

import me.illusion.skyblockcore.shared.dependency.JedisUtil;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketProcessor;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class RedisMessaging extends BinaryJedisPubSub implements PacketProcessor {

    private static final byte[] KEY = "SkyblockChannel".getBytes(StandardCharsets.UTF_8);

    private final SkyblockPlugin main;
    private final JedisUtil jedisUtil;

    public RedisMessaging(SkyblockPlugin main) {
        this.main = main;
        jedisUtil = new JedisUtil();

        FileConfiguration config = main.getFiles().getSettings().getConfiguration();
        String ip = config.getString("communication.host");
        String port = config.getString("communication.port", "");
        String password = config.getString("communication.password", "");

        if (!jedisUtil.connect(ip, port, password)) {
            return;
        }

        Jedis jedis = jedisUtil.getJedis();

        new Thread(() -> jedis.subscribe(this, KEY)).start();
    }

    public void updatePlayer(UUID uuid, String proxy) {
        Jedis jedis = jedisUtil.getJedis();
        jedis.set(uuid.toString().getBytes(StandardCharsets.UTF_8), proxy.getBytes(StandardCharsets.UTF_8));
        jedisUtil.getPool().returnResource(jedis);
    }

    public String getProxy(UUID uuid) {
        Jedis jedis = jedisUtil.getJedis();
        byte[] bytes = jedis.get(uuid.toString().getBytes(StandardCharsets.UTF_8));
        jedisUtil.getPool().returnResource(jedis);

        return new String(bytes);
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        if (!Arrays.equals(channel, KEY))
            return;

        main.getPacketManager().read(message);
    }

    @Override
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {

    }

    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {

    }

    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {

    }

    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {

    }

    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {

    }

    @Override
    public void send(Packet packet) {
        Jedis jedis = jedisUtil.getJedis();
        try {
            jedis.publish(KEY, packet.getAllBytes());
        } catch (Exception e) {
            jedis = jedisUtil.getJedis();
            jedis.publish(KEY, packet.getAllBytes());
        } finally {
            jedisUtil.getPool().returnResource(jedis);
        }
    }
}
