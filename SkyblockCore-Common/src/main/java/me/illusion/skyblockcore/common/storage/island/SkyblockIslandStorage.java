package me.illusion.skyblockcore.common.storage.island;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;

public interface SkyblockIslandStorage extends SkyblockDatabase {

    CompletableFuture<UUID> getIslandId(UUID profileId);

    CompletableFuture<IslandData> getIslandData(UUID islandId);

    CompletableFuture<Void> saveIslandData(IslandData data);

    CompletableFuture<Void> deleteIslandData(UUID islandId);

}
