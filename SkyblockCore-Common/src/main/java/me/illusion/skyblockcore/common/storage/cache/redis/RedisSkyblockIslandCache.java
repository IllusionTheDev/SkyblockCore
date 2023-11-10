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

    private static final String ISLANDS_KEY = "islands";
    
    @Override
    public CompletableFuture<Void> wipe() {
        return update(jedis -> jedis.hdel(ISLANDS_KEY, jedis.hkeys(ISLANDS_KEY).toArray(new String[0])));
    }

    @Override
    public CompletableFuture<Void> unloadServer() {
        return update(jedis -> jedis.hdel(ISLANDS_KEY, jedis.hkeys(ISLANDS_KEY).toArray(new String[0])));
    }

    @Override
    public CompletableFuture<Map<String, Collection<UUID>>> getAllIslands() {
        return query(jedis -> {
            Map<String, String> islands = jedis.hgetAll(ISLANDS_KEY);
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
        return query(jedis -> jedis.hget(ISLANDS_KEY, islandId.toString()));
    }

    @Override
    public CompletableFuture<Void> setServer(UUID islandId, String serverId) {
        return update(jedis -> jedis.hset(ISLANDS_KEY, islandId.toString(), serverId));
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return update(jedis -> jedis.hdel(ISLANDS_KEY, islandId.toString()));
    }

    @Override
    public CompletableFuture<Void> removeServer(String serverId) {
        return update(jedis -> {
            Map<String, String> islands = jedis.hgetAll(ISLANDS_KEY);

            for (Map.Entry<String, String> entry : islands.entrySet()) {
                if (entry.getValue().equals(serverId)) {
                    jedis.hdel(ISLANDS_KEY, entry.getKey());
                }
            }
        });
    }
}
