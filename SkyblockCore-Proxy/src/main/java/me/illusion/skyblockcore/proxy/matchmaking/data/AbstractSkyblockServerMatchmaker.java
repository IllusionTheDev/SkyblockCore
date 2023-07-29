package me.illusion.skyblockcore.proxy.matchmaking.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.database.cache.SkyblockCacheDatabase;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;
import me.illusion.skyblockcore.proxy.instance.ProxyServerData;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator;

/**
 * Represents a base implementation of a Skyblock server matchmaker. Each platform should extend this class and implement the
 * {@link #createData(String, Collection)} method.
 */
public abstract class AbstractSkyblockServerMatchmaker implements SkyblockServerMatchmaker {

    private final ServerDataComparator comparator;
    private final SkyblockCacheDatabase cacheDatabase;

    public AbstractSkyblockServerMatchmaker(SkyblockProxyPlatform platform) {
        this.comparator = platform.getMatchmakerComparatorRegistry().getDefaultComparator();
        this.cacheDatabase = platform.getDatabaseRegistry().getChosenCacheDatabase();
    }

    @Override
    public CompletableFuture<String> matchMake(UUID islandId) {
        if (islandId == null) { // If the player doesn't have an island, just matchmake for all islands.
            return matchMakeAllIslands();
        }

        return cacheDatabase.getIslandServer(islandId).thenCompose(serverId -> {
            if (serverId != null) {
                return CompletableFuture.completedFuture(serverId);
            }

            return matchMakeAllIslands();
        });
    }

    protected CompletableFuture<String> matchMakeAllIslands() {
        return cacheDatabase.getAllIslands().thenApply(islands -> {
            List<ProxyServerData> servers = new ArrayList<>();

            for (Map.Entry<String, Collection<UUID>> entry : islands.entrySet()) {
                String serverId = entry.getKey();
                Collection<UUID> islandIds = entry.getValue();

                servers.add(createData(serverId, islandIds));
            }

            servers.sort(comparator);

            return servers.get(0).getName();
        });
    }

    /**
     * Creates a new {@link ProxyServerData} instance.
     *
     * @param server    the server id.
     * @param islandIds the island ids.
     * @return the new {@link ProxyServerData} instance.
     */
    public abstract ProxyServerData createData(String server, Collection<UUID> islandIds);
}
