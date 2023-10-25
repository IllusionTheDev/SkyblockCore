package me.illusion.skyblockcore.common.storage.cache.redis;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.database.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.storage.cache.SkyblockIslandCache;

public class MemorySkyblockIslandCache extends AbstractSkyblockDatabase implements SkyblockIslandCache {

    private final Map<String, Collection<UUID>> islands = new ConcurrentHashMap<>();

    public MemorySkyblockIslandCache() {
        addTag(SkyblockDatabaseTag.LOCAL);
        addTag(SkyblockDatabaseTag.CACHE);
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection properties) {
        setProperties(properties);
        return CompletableFuture.completedFuture(true); // Prevent any exceptions regarding not overriding the enable method
    }

    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public CompletableFuture<Void> wipe() {
        islands.clear();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> unloadServer() {
        return wipe();
    }

    @Override
    public CompletableFuture<Map<String, Collection<UUID>>> getAllIslands() {
        return CompletableFuture.completedFuture(islands);
    }

    @Override
    public CompletableFuture<String> getIslandServer(UUID islandId) {
        for (Map.Entry<String, Collection<UUID>> entry : islands.entrySet()) {
            if (entry.getValue().contains(islandId)) {
                return CompletableFuture.completedFuture(entry.getKey());
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> setServer(UUID islandId, String serverId) {
        islands.computeIfAbsent(serverId, k -> ConcurrentHashMap.newKeySet()).add(islandId);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        for (Map.Entry<String, Collection<UUID>> entry : islands.entrySet()) {
            entry.getValue().remove(islandId);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> removeServer(String serverId) {
        islands.remove(serverId);
        return CompletableFuture.completedFuture(null);
    }
}
