package me.illusion.skyblockcore.common.database;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkyblockCacheDatabase {

    CompletableFuture<String> getIslandServer(UUID islandId);

    CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId);

    CompletableFuture<Void> removeIsland(UUID islandId);

    CompletableFuture<Void> removeServer(String serverId);

}
