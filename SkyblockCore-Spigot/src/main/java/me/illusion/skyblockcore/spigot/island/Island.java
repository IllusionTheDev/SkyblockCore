package me.illusion.skyblockcore.spigot.island;

import java.util.UUID;
import me.illusion.cosmos.session.CosmosSession;
import me.illusion.skyblockcore.common.data.IslandData;
import org.bukkit.Location;

/**
 * Represents an instanced Island. This should be a data class, with little to no logic. The logic should be handled by the IslandManager. The lifecycle of an
 * island is tied to a CosmosSession, which means that if the session is destroyed, the island is destroyed.
 */
public class Island {

    private final IslandData data;
    private final CosmosSession session;

    public Island(IslandData data, CosmosSession session) {
        this.data = data;
        this.session = session;
    }

    public IslandData getData() {
        return data;
    }

    public UUID getIslandId() {
        return data.getIslandId();
    }

    public Location getCenter() {
        return session.getPastedArea().getPasteLocation();
    }

}
