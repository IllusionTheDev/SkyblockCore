package me.illusion.skyblockcore.common.databaserewrite.cache.island;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkyblockIslandCache {

    CompletableFuture<Map<String, Collection<UUID>>> getAllIslands();

    CompletableFuture<String> getIslandServer(UUID islandId);

    CompletableFuture<Void> setServer(UUID islandId, String serverId);

    CompletableFuture<Void> removeServer(UUID islandId);

    CompletableFuture<Void> removeServer(String serverId);

    CompletableFuture<Void> unloadServer();

}
