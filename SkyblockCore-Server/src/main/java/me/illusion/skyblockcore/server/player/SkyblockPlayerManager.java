package me.illusion.skyblockcore.server.player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkyblockPlayerManager {

    SkyblockPlayer getPlayer(UUID playerId);

    SkyblockPlayer getPlayerFromProfile(UUID profileId);

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
     * This method is used to get the cached profile ID for a player.
     *
     * @param playerId The player ID to get the cached profile ID for.
     * @return The cached profile ID for the player.
     */
    UUID getCachedProfileId(UUID playerId);

}
