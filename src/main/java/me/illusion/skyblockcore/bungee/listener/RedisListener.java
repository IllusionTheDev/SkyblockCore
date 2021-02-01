package me.illusion.skyblockcore.bungee.listener;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.utilities.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisListener extends JedisPubSub {

    private final SkyblockBungeePlugin main;
    private final Jedis jedis;

    public RedisListener(SkyblockBungeePlugin main) {
        this.main = main;
        this.jedis = main.getJedisUtil().getJedis();
        jedis.subscribe(this, "SkyblockCommunication");
    }

    public void register(UUID islandId, String server) {
        jedis.sadd("Islands", islandId + "-" + server);
    }

    public void remove(UUID islandId, String server) {
        jedis.srem("Islands", islandId + "-" + server);
    }

    public void requestUpdate() {
        CompletableFuture.runAsync(() -> jedis.publish("SkyblockCommunication", "UPDATE"));
    }

    public void update() {
        CompletableFuture.runAsync(() -> {
            for (String island : jedis.smembers("Islands")) {
                String[] split = StringUtil.split(island, '-');

                String islandId = split[0];
                String server = split[1];

                main.getPlayerFinder().update(UUID.fromString(islandId), server);
            }
        });

    }

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals("SkyblockCommunication") && message.equals("UPDATE"))
            update();
    }
}
