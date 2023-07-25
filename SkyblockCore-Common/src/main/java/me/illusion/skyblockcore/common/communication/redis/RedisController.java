package me.illusion.skyblockcore.common.communication.redis;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * The redis controller class is responsible for handling redis connections, providing supply and borrow methods to easily interact with the Jedis API.
 */
public class RedisController {

    private final JedisPool pool;
    private final String password;

    public RedisController(JedisPool pool, String password) {
        this.pool = pool;
        this.password = password;
    }

    public RedisController(String host, int port, String password, boolean ssl) {
        this(new JedisPool(host, port, ssl), password);
    }

    /**
     * Borrow a jedis instance from the pool, and run the consumer on it. The jedis instance will be closed after the consumer is done.
     *
     * @param consumer The consumer to run on the jedis instance
     * @return A completable future that will be completed when the consumer is done
     */
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

    /**
     * Borrow a jedis instance from the pool, and run the function on it. The jedis instance will be closed after the function is done.
     *
     * @param function The function to run on the jedis instance
     * @param <T>      The return type of the function
     * @return A completable future that will be completed when the function is done, and will contain the return value of the function
     */
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

    /**
     * Get a jedis instance from the pool. The jedis instance will not be closed, and must be closed manually.
     *
     * @return A jedis instance
     */
    public Jedis getJedis() {
        Jedis jedis = pool.getResource();

        if (password != null) {
            jedis.auth(password);
        }

        return jedis;
    }

    /**
     * Check if the redis connection is valid
     *
     * @return Whether the redis connection is valid
     */
    public boolean isValid() {
        try (Jedis jedis = getJedis()) {
            return jedis.ping() != null;
        }
    }
}
