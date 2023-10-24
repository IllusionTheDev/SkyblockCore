package me.illusion.skyblockcore.common.databaserewrite.cache.island;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.databaserewrite.cache.SkyblockCache;

public interface SkyblockIslandCache extends SkyblockCache {

    CompletableFuture<Map<String, Collection<UUID>>> getAllIslands();

    CompletableFuture<String> getIslandServer(UUID islandId);

    CompletableFuture<Void> setServer(UUID islandId, String serverId);

    CompletableFuture<Void> removeIsland(UUID islandId);

    CompletableFuture<Void> removeServer(String serverId);

}
