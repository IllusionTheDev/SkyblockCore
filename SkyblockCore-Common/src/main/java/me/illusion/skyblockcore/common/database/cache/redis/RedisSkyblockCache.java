package me.illusion.skyblockcore.common.database.cache.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import me.illusion.skyblockcore.common.communication.redis.RedisController;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import redis.clients.jedis.Jedis;

/**
 * A redis implementation of {@link SkyblockCacheDatabase}
 */
public class RedisSkyblockCache implements SkyblockCacheDatabase {

    private final Set<CompletableFuture<?>> futures = ConcurrentHashMap.newKeySet();
    private RedisController controller;

    private static final String ISLAND_SERVERS_KEY = "island-servers";

    @Override
    public String getName() {
        return "redis";
    }

    @Override
    public CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties) {
        return CompletableFuture.supplyAsync(() -> {
            String host = properties.getString("host", "localhost");
            int port = properties.getInt("port", 6379);
            String password = properties.getString("password");
            boolean ssl = properties.getBoolean("ssl", false);

            controller = new RedisController(host, port, password, ssl);

            return controller.isValid();
        });
    }

    @Override
    public CompletableFuture<String> getIslandServer(UUID islandId) {
        return associate(jedis -> jedis.hget(ISLAND_SERVERS_KEY, islandId.toString()));
    }

    @Override
    public CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId) {
        return associateTask(jedis -> jedis.hset(ISLAND_SERVERS_KEY, islandId.toString(), serverId));
    }

    @Override
    public CompletableFuture<Void> removeServer(String serverId) {
        return associateTask(jedis -> jedis.hdel(ISLAND_SERVERS_KEY, serverId));
    }

    @Override
    public CompletableFuture<Collection<UUID>> getIslands(String serverId) {
        return associate(jedis -> jedis.hkeys(ISLAND_SERVERS_KEY)).thenApply(strings -> {
            List<UUID> islands = new ArrayList<>();

            for (String string : strings) {
                islands.add(UUID.fromString(string));
            }

            return islands;
        });
    }

    @Override
    public CompletableFuture<Map<String, Collection<UUID>>> getAllIslands() {
        return associate(jedis -> jedis.hgetAll(ISLAND_SERVERS_KEY)).thenApply(stringMap -> {
            Map<String, Collection<UUID>> map = new ConcurrentHashMap<>();

            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                String islandId = entry.getKey();
                String serverId = entry.getValue();

                map.computeIfAbsent(serverId, k -> new ArrayList<>()).add(UUID.fromString(islandId));
            }

            return map;
        });
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return associateTask(jedis -> jedis.hdel(ISLAND_SERVERS_KEY, islandId.toString()));
    }

    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private <T> CompletableFuture<T> associate(Function<Jedis, T> function) {
        CompletableFuture<T> future = controller.supply(function);

        futures.add(future);

        future.thenRun(() -> futures.remove(future));
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return future;
    }

    private CompletableFuture<Void> associateTask(Consumer<Jedis> consumer) {
        CompletableFuture<Void> future = controller.borrow(consumer);

        futures.add(future);

        future.thenRun(() -> futures.remove(future));
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return future;
    }

}
