package me.illusion.skyblockcore.server.island;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.utilities.time.Time;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;
import me.illusion.skyblockcore.server.util.SkyblockLocation;

/**
 * The skyblock island manager is responsible for loading and unloading skyblock islands. Each platform should implement their own island manager, which will
 * handle the loading and unloading of islands.
 */
public interface SkyblockIslandManager {

    /**
     * Load an island from data.
     *
     * @param data The data to load from.
     * @return The loaded island.
     */
    CompletableFuture<SkyblockIsland> loadIsland(IslandData data);

    /**
     * Load a player's island.
     *
     * @param profileId The profile id of the player.
     * @param fallback  The fallback island to load if the player does not have an island.
     * @return The loaded island.
     */
    CompletableFuture<SkyblockIsland> loadPlayerIsland(UUID profileId, String fallback);

    /**
     * Create an island.
     *
     * @param template  The template to use.
     * @param profileId The profile id of the player.
     * @return The created island.
     */
    CompletableFuture<SkyblockIsland> createIsland(String template, UUID profileId);

    /**
     * Get the island data for a profile.
     *
     * @param profileId The profile id of the player.
     * @return The island data.
     */
    CompletableFuture<IslandData> getIslandData(UUID profileId);

    /**
     * Force unload an island.
     *
     * @param islandId The island id.
     * @param save     Whether or not to save the island.
     * @return A future which completes when the island is unloaded.
     */
    CompletableFuture<Void> forceUnloadIsland(UUID islandId, boolean save);

    /**
     * Request an island to be unloaded. If there is a request to load the island, the unload will be cancelled.
     *
     * @param islandId    The island id.
     * @param save        Whether or not to save the island.
     * @param unloadDelay The delay before unloading the island.
     * @return A future which completes when the island is unloaded, or when the request is cancelled.
     */
    CompletableFuture<Boolean> requestUnloadIsland(UUID islandId, boolean save, Time unloadDelay);

    /**
     * Load an island.
     *
     * @param islandId The island id.
     * @return The loaded island.
     */
    CompletableFuture<SkyblockIsland> loadIsland(UUID islandId);

    /**
     * Disable the island manager.
     *
     * @param save  Whether to save the islands.
     * @param async Whether to disable asynchronously. Bukkit does not allow disabling asynchronously, so it sets to false.
     * @return A future which completes when the island manager is disabled.
     */
    CompletableFuture<Void> disable(boolean save, boolean async);

    /**
     * Flush the island manager. This will process all pending requests.
     *
     * @return A future which completes when the island manager is flushed.
     */
    CompletableFuture<Void> flush();

    /**
     * Get a loaded island.
     *
     * @param islandId The island id.
     * @return The loaded island.
     */
    SkyblockIsland getLoadedIsland(UUID islandId);

    /**
     * Get a profile's island.
     *
     * @param profileId The profile id.
     * @return The profile's island.
     */
    SkyblockIsland getProfileIsland(UUID profileId);

    /**
     * Get a player's island.
     *
     * @param playerId The player id.
     * @return The player's island.
     */
    SkyblockIsland getPlayerIsland(UUID playerId);

    /**
     * Get a player's island.
     *
     * @param player The SkyblockPlayer.
     * @return The player's island.
     */
    default SkyblockIsland getPlayerIsland(SkyblockPlayer player) {
        return getPlayerIsland(player.getUniqueId());
    }

    /**
     * Get the island at a location.
     *
     * @param location The location.
     * @return The island at the location.
     */
    SkyblockIsland getIslandAt(SkyblockLocation location);

    /**
     * Gets the amount of loaded islands, useful for metrics.
     *
     * @return The amount of loaded islands.
     */
    int getLoadedIslandCount();
}
