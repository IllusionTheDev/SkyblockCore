package me.illusion.skyblockcore.proxy.matchmaking.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a matchmaker for Skyblock servers. The matchmaker is responsible for finding the best server to send a player to.
 */
public interface SkyblockServerMatchmaker {

    /**
     * Gets the platform this matchmaker is running on.
     *
     * @param islandId the island id to matchmake for.
     * @return the server id to send the player to.
     */
    CompletableFuture<String> matchMake(UUID islandId);

}
