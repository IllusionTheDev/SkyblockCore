package me.illusion.skyblockcore.spigot.utilities.region;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

/**
 * A utility class for flood-filling areas. Uses a multithreaded approach, benefiting from a chunk snapshot cache.
 */
public final class AreaCreator {

    private static final Map<Long, ChunkSnapshot> chunkCache = new ConcurrentHashMap<>();
    private static final Map<Long, CompletableFuture<ChunkSnapshot>> chunkFutures = new ConcurrentHashMap<>();

    private AreaCreator() {

    }

    /**
     * Flood fills a region of blocks with the same material
     *
     * @param origin The origin location to start the flood fill from.
     * @return A completablefuture of the MineableArea object. This is done asynchronously.
     */
    public static CompletableFuture<Set<BlockVector>> floodFill(Location origin) {
        Set<BlockVector> vectors = Sets.newConcurrentHashSet();
        Set<BlockVector> visited = Sets.newConcurrentHashSet();

        Set<Long> visitedChunks = Sets.newConcurrentHashSet(); // Instead of wiping the cache, we can just keep track of the chunks we've visited. This should prevent issues with multiple areas being created at once.

        Deque<Runnable> tasks = new ConcurrentLinkedDeque<>();

        return CompletableFuture.supplyAsync(() -> {
            for (BlockFace face : BlockFace.values()) {
                floodFillAsync(visitedChunks, tasks, origin.getBlock().getType(), origin, face, vectors, visited);
            }

            while (!tasks.isEmpty()) {
                tasks.poll().run();
            }

            for (long chunk : visitedChunks) {
                chunkCache.remove(chunk);
                chunkFutures.remove(chunk); // shouldn't be needed, but just in case
            }

            return vectors;
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    /**
     * Runs an async recursive flood fill on a given block face.
     *
     * @param visitedChunks A collection of chunk keys that have been visited.
     * @param tasks         A collection of tasks to run. (This is used to prevent stack overflows)
     * @param type          The type of block to flood fill.
     * @param origin        The origin location to start the flood fill from.
     * @param face          The block face to flood fill.
     * @param vectors       The collection of vectors to add to.
     * @param visited       The collection of vectors that have been visited.
     */
    private static void floodFillAsync(Set<Long> visitedChunks, Deque<Runnable> tasks, Material type, Location origin, BlockFace face, Set<BlockVector> vectors,
        Set<BlockVector> visited) {
        BlockVector vector = origin.toVector().toBlockVector();

        if (visited.contains(vector)) {
            return; // Avoid infinite loops
        }

        ChunkSnapshot snapshot = getSnapshotFast(origin); // Get the chunk snapshot for the origin location
        long key = getChunkKey(origin); // Get the chunk key for the origin location

        visitedChunks.add(key); // Register the chunk as visited, this is used to clear the cache later.
        visited.add(vector); // Register the vector as visited, this is used to prevent infinite loops.

        if (snapshot.getBlockType(vector.getBlockX() & 0xF, vector.getBlockY(), vector.getBlockZ() & 0xF) != type) {
            return; // If the block type is not the same as the origin, return.
        }

        vectors.add(vector); // Add the vector to the collection of valid blocks.

        List<CompletableFuture<Void>> futures = new ArrayList<>(); // Create a list of futures to wait for, this way we can run multi-thread tasks without stack overflows.
        for (BlockFace face2 : BlockFace.values()) { // Loop through all block faces
            if (face2 == face.getOppositeFace()) {
                continue; // If the face is the opposite of the current face, skip it.
            }

            Location newLocation = origin.getBlock().getRelative(face2).getLocation(); // Get the new location to flood fill from.
            BlockVector newVector = newLocation.toVector().toBlockVector(); // Get the new vector to flood fill from.
            if (visited.contains(newVector)) {
                continue; // If the vector has already been visited, skip it.
            }

            futures.add(CompletableFuture.runAsync(
                () -> floodFillAsync(visitedChunks, tasks, type, newLocation, face2, vectors, visited))); // Calling CF again to further parallelize

        }

        // Adds a task to the task queue, that waits for all futures to complete
        tasks.add(() -> CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join());
    }

    /**
     * This method obtains a chunk snapshot asynchonously, and caches it. If the chunk is already cached, it will return the cached snapshot. If the chunk is
     * not cached, it blocks the current thread until the snapshot is obtained on the main thread.
     *
     * @param location The location to get the chunk snapshot for.
     * @return The chunk snapshot.
     */
    private static ChunkSnapshot getSnapshotFast(Location location) {
        long key = getChunkKey(location);

        if (chunkCache.containsKey(key)) {
            return chunkCache.get(key);
        }

        if (chunkFutures.containsKey(key)) {
            return chunkFutures.get(key).join();
        }

        if (Bukkit.isPrimaryThread()) {
            ChunkSnapshot snapshot = location.getChunk().getChunkSnapshot();
            chunkCache.put(key, snapshot);
            return snapshot;
        }

        CompletableFuture<ChunkSnapshot> future = CompletableFuture.supplyAsync(() -> getSnapshotFast(location), MainThreadExecutor.INSTANCE);

        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
        chunkFutures.put(key, future);
        return future.join();
    }

    /**
     * Gets a chunk key for a given location.
     *
     * @param location The location to get the chunk key for.
     * @return The chunk key.
     */
    private static long getChunkKey(Location location) {
        int x = location.getBlockX() >> 4;
        int z = location.getBlockZ() >> 4;

        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

}
