package me.illusion.skyblockcore.bungee.utilities.database;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.updating.Updater;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.IOException;

public class JedisUtil {

    private JedisPool jedisPool;

    private String password; // NOT SAFE but it's a configurable value so

    public boolean connect(SkyblockBungeePlugin main, String ip, String port, String password) {
        if (!checkJedis(main))
            return false;

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
            j.auth(password);

        return j;
    }

    private boolean checkJedis(SkyblockBungeePlugin main) {
        try {
            Class.forName("redis.clients.jedis.Jedis");
            return true;
        } catch (ClassNotFoundException e) {
            File pluginsFolder = main.getDataFolder().getParentFile();
            new Updater(pluginsFolder, 88516, createJedisFile(pluginsFolder));
            return false;
        }
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
