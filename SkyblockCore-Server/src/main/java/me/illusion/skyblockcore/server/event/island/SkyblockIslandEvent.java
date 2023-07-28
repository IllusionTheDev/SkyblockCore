package me.illusion.skyblockcore.server.event.island;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.server.island.SkyblockIsland;

/**
 * This class is a basic tag class for skyblock island events. It is expected that all skyblock island events extend this class.
 */
public abstract class SkyblockIslandEvent extends SkyblockEvent {

    private final SkyblockIsland island;

    public SkyblockIslandEvent(SkyblockIsland island) {
        this.island = island;
    }

    /**
     * Gets the SkyblockIsland that was involved in this event.
     *
     * @return The SkyblockIsland that was involved in this event.
     */
    public SkyblockIsland getIsland() {
        return island;
    }

}
