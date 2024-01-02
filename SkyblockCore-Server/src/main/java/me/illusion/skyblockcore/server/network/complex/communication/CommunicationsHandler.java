package me.illusion.skyblockcore.server.network.complex.communication;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.common.storage.cache.SkyblockIslandCache;
import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.server.network.complex.communication.packet.response.PacketResponseIslandTeleport;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * Handles communications between instances. This class is responsible for handling island requests, teleport requests, and other communications.
 */
public class CommunicationsHandler { // Potential problem: If an island is requested to be loaded in 2 instances at the same time, it will be loaded twice.

    private final PacketManager packetManager;
    private final String serverId;
    private final SkyblockIslandCache cacheDatabase;

    private final ComplexSkyblockNetwork network;

    public CommunicationsHandler(ComplexSkyblockNetwork network) {
        this.network = network;

        this.cacheDatabase = network.getCacheDatabase();
        this.serverId = network.getConfiguration().getServerId();

        // We need to init the other values
        // TODO: Write a config for this
        packetManager = null;
    }

    /**
     * Disables the communications handler. This will clean up any cached data in the database.
     */
    public void disable() {
        cacheDatabase.removeServer(serverId).join();
    }

    /**
     * Fetches the server ID of where an island is loaded
     *
     * @param islandId The island ID
     * @return A future containing the server ID, which may be null
     */
    public CompletableFuture<String> getIslandServer(UUID islandId) {
        return cacheDatabase.getIslandServer(islandId);
    }

    /**
     * Updates the server ID of where an island is loaded
     *
     * @param islandId The island ID
     * @param serverId The server ID
     * @return A future containing the result of the update
     */
    public CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId) {
        return cacheDatabase.setServer(islandId, serverId);
    }

    /**
     * Updates the server ID of where an island is loaded, using the current server ID
     *
     * @param island The island
     * @return A future containing the result of the update
     */
    public CompletableFuture<Void> updateIslandServer(SkyblockIsland island) {
        return updateIslandServer(island.getIslandId(), serverId);
    }

    /**
     * Removes an island from the cache database
     *
     * @param islandId The island ID
     * @return A future containing the result of the removal
     */
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return cacheDatabase.removeIsland(islandId);
    }

    /**
     * Checks to see if an island can be loaded on this instance
     *
     * @param islandId The island ID
     * @return A future containing the result of the check
     */
    public CompletableFuture<Boolean> canLoad(UUID islandId) {
        if (islandId == null) {
            return CompletableFuture.completedFuture(true);
        }

        return getIslandServer(islandId).thenApply(instanceId -> instanceId == null || instanceId.equals(serverId));
    }

    // -- GENERAL STUFF --

    /**
     * Attempts to teleport a player to an island
     *
     * @param player   The player
     * @param islandId The island ID
     * @return A future containing the result of the teleport
     */
    public CompletableFuture<Boolean> attemptTeleportToIsland(SkyblockPlayer player, UUID islandId) {
        boolean cached = tryTeleportExisting(player, islandId);

        if (cached) {
            return CompletableFuture.completedFuture(true);
        }

        return getIslandServer(islandId).thenCompose(instanceId -> {
            if (instanceId == null || instanceId.equals(serverId)) {
                return network.getIslandManager().loadIsland(islandId).thenApply(island -> tryTeleportExisting(player, islandId));
            }

            packetManager.send(serverId,
                new PacketRequestIslandTeleport(this.serverId, player.getUniqueId(), islandId)); // This is the packet that is sent to the other server.

            return packetManager.await(
                    PacketResponseIslandTeleport.class,
                    packet -> packet.getPlayerId().equals(player.getUniqueId()) // Filter the player ID we're looking for
                )
                .thenApply(packet -> packet != null && packet.isAllowed());
        });
    }

    /**
     * Attempts to teleport a player to an existing island, if it is loaded
     *
     * @param player   The player
     * @param islandId The island ID
     * @return Whether or not the player was teleported
     */
    private boolean tryTeleportExisting(SkyblockPlayer player, UUID islandId) {
        SkyblockIsland cached = network.getIslandManager().getLoadedIsland(islandId);

        if (cached != null) {
            player.teleport(cached.getCenter());
            return true;
        }

        return false;
    }

    // -- PACKET HANDLERS --

    public PacketManager getPacketManager() {
        return packetManager;
    }
}
