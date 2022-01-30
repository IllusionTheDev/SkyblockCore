package me.illusion.skyblockcore.bungee.utilities.database;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.IOException;

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


    private File createJedisFile(File folder) {
        File file = new File(folder, "JedisDependency-1.0-all.jar");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public JedisPool getPool() {
        return jedisPool;
    }
}
