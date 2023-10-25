package me.illusion.skyblockcore.common.storage.island;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.storage.SkyblockStorage;

public interface SkyblockIslandStorage extends SkyblockStorage<SkyblockIslandStorage> {

    CompletableFuture<UUID> getIslandId(UUID profileId);

    CompletableFuture<IslandData> getIslandData(UUID islandId);

    CompletableFuture<Void> saveIslandData(IslandData data);

    CompletableFuture<Void> deleteIslandData(UUID islandId);

    // For migration purposes

    CompletableFuture<Collection<IslandData>> getAllIslandData();

    CompletableFuture<Void> saveAllIslandData(Collection<IslandData> data);

    default CompletableFuture<Void> migrateTo(SkyblockIslandStorage other) {
        return getAllIslandData().thenCompose(other::saveAllIslandData);
    }
}
