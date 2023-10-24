package me.illusion.skyblockcore.common.storage.profiles;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.storage.SkyblockStorage;

public interface SkyblockProfileStorage extends SkyblockStorage<SkyblockProfileStorage> {

    CompletableFuture<UUID> getProfileId(UUID playerId);

    CompletableFuture<Void> setProfileId(UUID playerId, UUID profileId);

    CompletableFuture<Map<UUID, UUID>> getAllProfileIds();

    CompletableFuture<Void> setAllProfileIds(Map<UUID, UUID> profileIds);

    default CompletableFuture<Void> migrateTo(SkyblockProfileStorage other) {
        return getAllProfileIds().thenCompose(other::setAllProfileIds);
    }
}
