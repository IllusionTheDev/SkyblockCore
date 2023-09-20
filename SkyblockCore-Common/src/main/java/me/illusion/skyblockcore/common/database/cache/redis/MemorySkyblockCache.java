package me.illusion.skyblockcore.common.database.cache.redis;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.database.cache.AbstractSkyblockCacheDatabase;

/**
 * Represents an in-memory implementation of a SkyblockCacheDatabase. This should not be used for anything other than a "simple" network structure.
 */
public class MemorySkyblockCache extends AbstractSkyblockCacheDatabase {

    public MemorySkyblockCache() {
        addTag(SkyblockDatabaseTag.CACHE);
        addTag(SkyblockDatabaseTag.LOCAL);
    }

    private final Map<UUID, String> islandServers = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> getIslandServer(UUID islandId) {
        return CompletableFuture.completedFuture(islandServers.get(islandId));
    }

    @Override
    public CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId) {
        return CompletableFuture.runAsync(() -> islandServers.put(islandId, serverId));
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return CompletableFuture.runAsync(() -> islandServers.remove(islandId));
    }

    @Override
    public CompletableFuture<Void> removeServer(String serverId) {
        return CompletableFuture.runAsync(() -> islandServers.values().removeIf(serverId::equals));
    }

    @Override
    public CompletableFuture<Collection<UUID>> getIslands(String serverId) {
        Set<UUID> islands = ConcurrentHashMap.newKeySet();

        for (Map.Entry<UUID, String> entry : islandServers.entrySet()) {
            if (entry.getValue().equals(serverId)) {
                islands.add(entry.getKey());
            }
        }

        return CompletableFuture.completedFuture(islands);
    }

    @Override
    public CompletableFuture<Map<String, Collection<UUID>>> getAllIslands() {
        Map<String, Collection<UUID>> islands = new ConcurrentHashMap<>();

        for (Map.Entry<UUID, String> entry : islandServers.entrySet()) {
            islands.computeIfAbsent(entry.getValue(), key -> ConcurrentHashMap.newKeySet()).add(entry.getKey());
        }

        return CompletableFuture.completedFuture(islands);
    }
}
