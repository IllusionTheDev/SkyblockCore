package me.illusion.skyblockcore.spigot.island;

import java.util.UUID;
import me.illusion.cosmos.session.CosmosSession;
import me.illusion.skyblockcore.common.data.IslandData;
import org.bukkit.Location;

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
