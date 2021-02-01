package me.illusion.skyblockcore.bungee.listener;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.utilities.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisListener extends JedisPubSub {

    private final Jedis jedis;
    private final SkyblockBungeePlugin main;

    public RedisListener(SkyblockBungeePlugin main) {
        this.main = main;
        this.jedis = main.getJedisUtil().getJedis();
        jedis.subscribe(this, "SkyblockCommunication");
    }

    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equals("SkyblockCommunication"))
            return;

        String[] split = StringUtil.split(message, ' ');

        if (!split[0].equals("GET_ISLAND"))
            return;

        UUID uuid = UUID.fromString(split[1]);

        main.getPlayerFinder().request(uuid).whenCompleteAsync((s, thr) -> {
            jedis.publish("SkyblockCommunication", s);

            if (thr != null)
                thr.printStackTrace();
        });
    }
}
