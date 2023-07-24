package me.illusion.skyblockcore.common.database.structure;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import me.illusion.skyblockcore.common.data.IslandData;

public interface SkyblockDatabase {

    /**
     * Obtains the name of this database
     *
     * @return The name
     */
    String getName();

    /**
     * Enables the database
     *
     * @param properties The properties, such as the host, port, username, password, etc.
     * @return A future
     */
    CompletableFuture<Boolean> enable(Map<String, ?> properties);

    /**
     * Fetches the island id of a player
     *
     * @param playerId The player's id
     * @return The island id
     */
    CompletableFuture<UUID> fetchIslandId(UUID playerId);

    /**
     * Fetches the island data of an island
     *
     * @param islandId The island's id
     * @return The island data
     */
    CompletableFuture<IslandData> fetchIslandData(UUID islandId);

    /**
     * Saves the island data
     *
     * @param data The island data
     * @return A future
     */
    CompletableFuture<Void> saveIslandData(IslandData data);

    /**
     * Deletes the island data
     *
     * @param islandId The island's id
     * @return A future
     */
    CompletableFuture<Void> deleteIslandData(UUID islandId);

    /**
     * Deletes the island data of a player
     *
     * @param playerId The player's id
     * @return A future
     */
    default CompletableFuture<Void> deletePlayerIsland(UUID playerId) {
        return compose(fetchIslandId(playerId), this::deleteIslandData);
    }

    /**
     * Fetches the island data of a player
     *
     * @param playerId The player's id
     * @return The island data
     */
    default CompletableFuture<IslandData> fetchPlayerIsland(UUID playerId) {
        return compose(fetchIslandId(playerId), this::fetchIslandData);
    }

    /**
     * Composes a future, returning null if the value is null
     *
     * @param future   The future
     * @param function The function
     * @param <T>      The type of the first future
     * @param <U>      The return type of the second future
     * @return The composed future
     */
    private <T, U> CompletableFuture<U> compose(CompletableFuture<T> future, Function<T, CompletableFuture<U>> function) {
        return future.thenCompose(value -> {
            if (value == null) {
                return CompletableFuture.completedFuture(null);
            }

            return function.apply(value);
        });
    }

    /**
     * Flushes the database
     *
     * @return A future
     */
    CompletableFuture<Void> flush();

    /**
     * Checks if the database is file based, meaning it is not a remote database and is not supported by complex networks
     *
     * @return If the database is file based
     */
    default boolean isFileBased() {
        return false;
    }

}