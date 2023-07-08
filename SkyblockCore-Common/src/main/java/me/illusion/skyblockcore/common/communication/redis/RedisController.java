package me.illusion.skyblockcore.common.communication.redis;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisController {

    private final JedisPool pool;
    private final String password;

    public RedisController(JedisPool pool, String password) {
        this.pool = pool;
        this.password = password;
    }

    public CompletableFuture<Void> borrow(Consumer<Jedis> consumer) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.auth(password);

                consumer.accept(jedis);
            }

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public <T> CompletableFuture<T> supply(Function<Jedis, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.auth(password);

                return function.apply(jedis);
            }

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public Jedis getJedis() {
        Jedis jedis = pool.getResource();

        if (password != null) {
            jedis.auth(password);
        }

        return jedis;
    }

    public boolean isValid() {
        try (Jedis jedis = getJedis()) {
            return jedis.ping() != null;
        }
    }
}
