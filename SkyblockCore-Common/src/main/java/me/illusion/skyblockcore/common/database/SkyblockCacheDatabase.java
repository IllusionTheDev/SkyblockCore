package me.illusion.skyblockcore.common.database;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.data.IslandData;

public interface SkyblockCacheDatabase {

    CompletableFuture<String> getIslandServer(UUID islandId);

    CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId);

    CompletableFuture<List<IslandData>> fetchAllIslandData();

    CompletableFuture<Void> insertIslandData(String serverId, IslandData data);

    CompletableFuture<Void> removeIsland(UUID islandId);

    CompletableFuture<Void> removeServer(String serverId);

}
