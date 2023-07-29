package me.illusion.skyblockcore.server.island;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.profile.SkyblockProfileCache;

/**
 * Abstract implementation of the SkyblockIslandManager
 */
public abstract class AbstractIslandManager implements SkyblockIslandManager {

    protected final Map<UUID, SkyblockIsland> loadedIslands = new ConcurrentHashMap<>();
    protected final Set<UUID> unloadingIslands = ConcurrentHashMap.newKeySet();

    protected final Set<CompletableFuture<?>> pending = ConcurrentHashMap.newKeySet();

    protected final SkyblockFetchingDatabase database;
    protected final SkyblockProfileCache profileCache;
    protected final SkyblockPlatform platform;

    public AbstractIslandManager(SkyblockPlatform platform) {
        this.platform = platform;

        this.database = platform.getDatabaseRegistry().getChosenDatabase();
        this.profileCache = platform.getProfileCache();
    }

    /**
     * Loads an island from a template
     *
     * @return The loaded island
     */
    @Override
    public CompletableFuture<SkyblockIsland> loadPlayerIsland(UUID profileId, String fallback) {
        SkyblockIsland cached = getProfileIsland(profileId);

        if (cached != null) { // Idiots
            return CompletableFuture.completedFuture(cached);
        }

        return register(database.fetchPlayerIsland(profileId).thenCompose(id -> {
            if (id == null) {
                return createIsland(fallback, profileId);
            }

            return loadIsland(id);
        }));
    }

    /**
     * Fetches the island data of a player
     *
     * @param profileId The player's id
     * @return A future
     */
    @Override
    public CompletableFuture<IslandData> getIslandData(UUID profileId) {
        SkyblockIsland cached = getProfileIsland(profileId);

        if (cached != null) {
            return CompletableFuture.completedFuture(cached.getData());
        }

        return register(database.fetchPlayerIsland(profileId));
    }

    /**
     * Loads an island
     *
     * @param islandId The island's id
     * @return A future
     */
    @Override
    public CompletableFuture<SkyblockIsland> loadIsland(UUID islandId) {
        return register(database.fetchIslandData(islandId).thenCompose(this::loadIsland));
    }


    /**
     * Registers a task to the pending list
     *
     * @param task The task
     * @param <T>  The task's type
     * @return The task
     */
    protected <T> CompletableFuture<T> register(CompletableFuture<T> task) {
        pending.add(task);
        return task.whenComplete((result, error) -> pending.remove(task));
    }

    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.allOf(pending.toArray(new CompletableFuture[0]));
    }

    // -------------- REGULAR METHODS -------------- //

    /**
     * Gets an island by its id
     *
     * @param islandId The island's id
     * @return The island
     */
    @Override
    public SkyblockIsland getLoadedIsland(UUID islandId) {
        return loadedIslands.get(islandId);
    }

    /**
     * Gets an island by its owner's id
     *
     * @param profileId The owner's id
     * @return The island
     */
    @Override
    public SkyblockIsland getProfileIsland(UUID profileId) {
        for (SkyblockIsland island : loadedIslands.values()) {
            if (island.getData().getOwnerId().equals(profileId)) {
                return island;
            }
        }

        return null;
    }

    @Override
    public SkyblockIsland getPlayerIsland(UUID playerId) {
        UUID cachedProfileId = profileCache.getCachedProfileId(playerId);

        if (cachedProfileId == null) {
            return null;
        }

        return getProfileIsland(cachedProfileId);
    }


}