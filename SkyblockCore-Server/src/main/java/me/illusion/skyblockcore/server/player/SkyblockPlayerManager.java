package me.illusion.skyblockcore.server.player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a managing class for player and profile data.
 */
public interface SkyblockPlayerManager {

    /**
     * This method is used to get a skyblock player from a player ID.
     *
     * @param playerId The player ID to get the skyblock player for.
     * @return The skyblock player.
     */
    SkyblockPlayer getPlayer(UUID playerId);

    /**
     * This method is used to get a skyblock player from a profile ID.
     *
     * @param profileId The profile ID to get the skyblock player for.
     * @return The skyblock player.
     */
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

    /**
     * Gets a snapshot of all the loaded players.
     *
     * @return A collection of all the loaded players.
     */
    Collection<SkyblockPlayer> getPlayers();

}
