package me.illusion.skyblockcore.common.profile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.Setter;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;

public abstract class AbstractSkyblockProfileCache implements SkyblockProfileCache {

    private final Map<UUID, UUID> profileMap = new ConcurrentHashMap<>();
    private final SkyblockFetchingDatabase database;

    @Setter
    private Function<UUID, UUID> profileMappingFunction = Function.identity(); // Player ID -> Player ID

    public AbstractSkyblockProfileCache(SkyblockFetchingDatabase database) {
        this.database = database;
    }

    public CompletableFuture<UUID> cacheProfileId(UUID playerId) {
        return fetchProfileId(playerId).thenCompose(profileId -> {
            if (profileId == null) {
                profileId = createProfileId(playerId);
                setIdInternally(playerId, profileId);

                UUID finalProfileId = profileId;
                return database.setProfileId(playerId, profileId).thenApply(irrelevant -> finalProfileId);
            }

            setIdInternally(playerId, profileId);
            return CompletableFuture.completedFuture(profileId);
        });
    }

    @Override
    public CompletableFuture<Void> saveProfileId(UUID playerId, UUID newProfileId) {
        return database.setProfileId(playerId, newProfileId).thenRun(() -> setIdInternally(playerId, newProfileId));
    }

    @Override
    public CompletableFuture<UUID> fetchProfileId(UUID playerId) {
        return profileMap.containsKey(playerId) ? CompletableFuture.completedFuture(profileMap.get(playerId))
            : database.getProfileId(playerId).thenApply(profileId -> {
                setIdInternally(playerId, profileId);
                return profileId;
            });
    }

    @Override
    public UUID createProfileId(UUID playerId) {
        return profileMappingFunction.apply(playerId);
    }

    @Override
    public UUID getCachedProfileId(UUID playerId) {
        return profileMap.get(playerId);
    }

    @Override
    public void deleteFromCache(UUID playerId) {
        profileMap.remove(playerId);
    }

    protected void setIdInternally(UUID playerId, UUID profileId) {
        profileMap.put(playerId, profileId);
    }

}
