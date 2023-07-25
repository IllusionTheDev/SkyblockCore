package me.illusion.skyblockcore.common.profile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This interface is used to cache profile IDs. Profile IDs allow for a player to have multiple profiles, and each profile is associated with a different
 * island.
 */
public interface SkyblockProfileCache {

    /**
     * This method is used to cache a profile ID for a player.
     *
     * @param playerId The player ID to cache the profile ID for.
     * @return A completable future that completes when the profile ID has been cached.
     */
    CompletableFuture<UUID> fetchProfileId(UUID playerId);

    /**
     * This method is used to save a profile ID for a player.
     *
     * @param playerId  The player ID to save the profile ID for.
     * @param profileId The profile ID to save.
     * @return A completable future that completes when the profile ID has been saved.
     */
    CompletableFuture<Void> saveProfileId(UUID playerId, UUID profileId);

    /**
     * This method is used to create a profile ID for a player.
     *
     * @param playerId The player ID to create the profile ID for.
     * @return The profile ID that was created.
     */
    UUID createProfileId(UUID playerId);

    /**
     * This method is used to get the cached profile ID for a player.
     *
     * @param playerId The player ID to get the cached profile ID for.
     * @return The cached profile ID for the player.
     */
    UUID getCachedProfileId(UUID playerId);

    /**
     * This method is used to delete a profile ID from the cache.
     *
     * @param playerId The player ID to delete the profile ID for.
     */
    void deleteFromCache(UUID playerId);

}
