package me.illusion.skyblockcore.common.storage.cache.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.database.cache.redis.AbstractRedisCacheDatabase;
import me.illusion.skyblockcore.common.storage.cache.SkyblockIslandCache;

public class RedisSkyblockIslandCache extends AbstractRedisCacheDatabase implements SkyblockIslandCache {

    @Override
    public CompletableFuture<Void> wipe() {
        return update(jedis -> {
            jedis.hdel("islands", jedis.hkeys("islands").toArray(new String[0]));
        });
    }

    @Override
    public CompletableFuture<Void> unloadServer() {
        return update(jedis -> {
            jedis.hdel("islands", jedis.hkeys("islands").toArray(new String[0]));
        });
    }

    @Override
    public CompletableFuture<Map<String, Collection<UUID>>> getAllIslands() {
        return query(jedis -> {
            Map<String, String> islands = jedis.hgetAll("islands");
            Map<String, Collection<UUID>> map = new HashMap<>();

            for (Map.Entry<String, String> entry : islands.entrySet()) {
                String serverId = entry.getValue();
                UUID islandId = UUID.fromString(entry.getKey());

                map.computeIfAbsent(serverId, k -> new ArrayList<>()).add(islandId);
            }

            return map;
        });
    }

    @Override
    public CompletableFuture<String> getIslandServer(UUID islandId) {
        return query(jedis -> {
            return jedis.hget("islands", islandId.toString());
        });
    }

    @Override
    public CompletableFuture<Void> setServer(UUID islandId, String serverId) {
        return update(jedis -> {
            jedis.hset("islands", islandId.toString(), serverId);
        });
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return update(jedis -> {
            jedis.hdel("islands", islandId.toString());
        });
    }

    @Override
    public CompletableFuture<Void> removeServer(String serverId) {
        return update(jedis -> {
            Map<String, String> islands = jedis.hgetAll("islands");

            for (Map.Entry<String, String> entry : islands.entrySet()) {
                if (entry.getValue().equals(serverId)) {
                    jedis.hdel("islands", entry.getKey());
                }
            }
        });
    }
}
