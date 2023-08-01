package me.illusion.skyblockcore.server.island;

import java.util.UUID;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.server.util.SkyblockCuboid;
import me.illusion.skyblockcore.server.util.SkyblockLocation;

/**
 * Represents an instanced Island. This should be a data class, with little to no logic. The logic should be handled by the IslandManager. The lifecycle of an
 * island is tied to a CosmosSession, which means that if the session is destroyed, the island is destroyed.
 */
public class SkyblockIsland {

    private final IslandData data;
    private final SkyblockLocation location;
    private final SkyblockCuboid bounds;

    public SkyblockIsland(IslandData data, SkyblockLocation location, SkyblockCuboid bounds) {
        this.data = data;
        this.location = location;
        this.bounds = bounds;
    }

    public IslandData getData() {
        return data;
    }

    public UUID getIslandId() {
        return data.getIslandId();
    }

    public SkyblockLocation getCenter() {
        return location;
    }

    public SkyblockCuboid getBounds() {
        return bounds;
    }

    public boolean contains(SkyblockLocation location) {
        return bounds.contains(location);
    }

}
