package me.illusion.skyblockcore.spigot.network.complex.communication;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import me.illusion.cosmos.utilities.concurrency.MainThreadExecutor;
import me.illusion.skyblockcore.common.communication.packet.PacketManager;
import me.illusion.skyblockcore.common.database.SkyblockCacheDatabase;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;
import me.illusion.skyblockcore.spigot.island.Island;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.request.PacketRequestIslandTeleport;
import me.illusion.skyblockcore.spigot.network.complex.communication.packet.response.PacketResponseIslandTeleport;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class CommunicationsHandler { // Potential problem: If an island is requested to be loaded in 2 instances at the same time, it will be loaded twice.

    private final PacketManager packetManager;
    private final String serverId;
    private final SkyblockCacheDatabase cacheDatabase;
    private final SkyblockDatabase database;

    private final ComplexSkyblockNetwork network;


    public void disable() {
        cacheDatabase.removeServer(serverId);
    }

    public CompletableFuture<String> getIslandServer(UUID islandId) {
        return cacheDatabase.getIslandServer(islandId);
    }

    public CompletableFuture<Void> updateIslandServer(UUID islandId, String serverId) {
        return cacheDatabase.updateIslandServer(islandId, serverId);
    }

    public CompletableFuture<Void> updateIslandServer(Island island) {
        return updateIslandServer(island.getIslandId(), serverId);
    }

    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return cacheDatabase.removeIsland(islandId);
    }

    public CompletableFuture<Boolean> canLoad(UUID islandId) {
        if (islandId == null) {
            return CompletableFuture.completedFuture(true);
        }

        return getIslandServer(islandId).thenApply(serverId -> serverId == null || serverId.equals(this.serverId));
    }

    // -- GENERAL STUFF --

    public CompletableFuture<Boolean> attemptTeleportToIsland(Player player, UUID islandId) {
        boolean cached = tryTeleportExisting(player, islandId);

        if (cached) {
            return CompletableFuture.completedFuture(true);
        }

        return getIslandServer(islandId).thenCompose(serverId -> {
            if (serverId == null || serverId.equals(this.serverId)) {
                return network.getIslandManager().loadIsland(islandId)
                    .thenApplyAsync(island -> tryTeleportExisting(player, islandId), MainThreadExecutor.INSTANCE);
            }

            packetManager.send(new PacketRequestIslandTeleport(player.getUniqueId(), islandId));
            return packetManager.await(PacketResponseIslandTeleport.class, packet -> packet.getPlayerId().equals(player.getUniqueId()))
                .thenApply(packet -> packet != null && packet.isAllowed());
        });
    }

    private boolean tryTeleportExisting(Player player, UUID islandId) {
        Island cached = network.getIslandManager().getLoadedIsland(islandId);

        if (cached != null) {
            player.teleport(cached.getCenter());
            return true;
        }

        return false;
    }

}
