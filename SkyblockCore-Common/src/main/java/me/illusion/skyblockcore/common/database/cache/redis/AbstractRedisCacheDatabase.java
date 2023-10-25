package me.illusion.skyblockcore.common.database.cache.redis;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.database.cache.AbstractCacheDatabase;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public abstract class AbstractRedisCacheDatabase extends AbstractCacheDatabase {

    private JedisPool pool;
    private String password;

    protected AbstractRedisCacheDatabase() {
        addTag(SkyblockDatabaseTag.REMOTE);
    }

    protected <T> CompletableFuture<T> query(Function<Jedis, T> function) {
        return associate(() -> {
            try (Jedis jedis = getJedis()) {
                return function.apply(jedis);
            }
        });
    }

    protected CompletableFuture<Void> update(Consumer<Jedis> consumer) {
        return associate(() -> {
            try (Jedis jedis = getJedis()) {
                consumer.accept(jedis);
            }
        });
    }

    protected Jedis getJedis() {
        Jedis jedis = pool.getResource();

        if (password != null) {
            jedis.auth(password);
        }

        return jedis;
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection properties) {
        setProperties(properties);

        String host = properties.getString("host");
        int port = properties.getInt("port");
        this.password = properties.getString("password");

        pool = new JedisPool(host, port);

        return query(jedis -> jedis.ping().equalsIgnoreCase("PONG"));
    }

    @Override
    public String getName() {
        return "redis";
    }
}
