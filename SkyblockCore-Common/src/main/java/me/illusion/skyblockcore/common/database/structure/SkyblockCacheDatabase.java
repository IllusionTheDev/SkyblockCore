package me.illusion.skyblockcore.common.database.structure;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkyblockCacheDatabase {

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

}
