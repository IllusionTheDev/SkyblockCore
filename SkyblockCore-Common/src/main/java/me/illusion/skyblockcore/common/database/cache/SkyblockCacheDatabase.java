package me.illusion.skyblockcore.common.database.cache;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;

/**
 * This interface represents a template for all caching databases. A caching database is responsible for caching the data that is currently in use, such as what
 * servers have what islands.
 */
public interface SkyblockCacheDatabase extends SkyblockDatabase {

    /**
     * Fetches the server id that has ownership of the island
     *
     * @param islandId The island's id
     * @return A future of the server id, may be null
     */
    CompletableFuture<String> getIslandServer(UUID islandId);

    /**
     * Updates the server id that has ownership of the island
     *
     * @param islandId The island's id
     * @param serverId The server's id
     * @return A future
     */
    CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId);

    /**
     * Removes the island from the cache, this is called when the island is unloaded
     *
     * @param islandId The island's id
     * @return A future which completes when the island is removed from the cache.
     */
    CompletableFuture<Void> removeIsland(UUID islandId);

    /**
     * Removes a server from the cache, which removes all islands that are owned by the server
     *
     * @param serverId The server's id
     * @return A future which completes when the server is removed from the cache.
     */
    CompletableFuture<Void> removeServer(String serverId);

    /**
     * Gets all the islands that are owned by the server
     *
     * @param serverId The server's id
     * @return A future of the islands
     */
    CompletableFuture<Collection<UUID>> getIslands(String serverId);

    /**
     * Gets all the islands that are owned by all servers
     *
     * @return A future of the islands
     */
    CompletableFuture<Map<String, Collection<UUID>>> getAllIslands();

    /**
     * Indicates whether or not the database is file based. Some network structures may refuse to load in this occasion.
     *
     * @return TRUE if the database is file based, FALSE otherwise
     */
    default boolean isFileBased() {
        return false;
    }

}
