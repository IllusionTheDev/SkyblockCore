package me.illusion.skyblockcore.shared.dependency;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisUtil {

    private JedisPool jedisPool;

    private String password; // NOT SAFE but it's a configurable value so

    public boolean connect(String ip, String port, String password) {
        if (port.isEmpty())
            jedisPool = new JedisPool(ip);
        else
            jedisPool = new JedisPool(ip, Integer.parseInt(port));

        this.password = password;
        return true;
    }

    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();

        if (!password.isEmpty())
            jedis.auth(password);

        return jedis;
    }

    public JedisPool getPool() {
        return jedisPool;
    }
}
