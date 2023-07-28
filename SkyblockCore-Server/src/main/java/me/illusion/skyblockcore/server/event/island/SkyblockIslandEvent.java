package me.illusion.skyblockcore.server.event.island;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.server.island.SkyblockIsland;

public abstract class SkyblockIslandEvent extends SkyblockEvent {

    private final SkyblockIsland island;

    public SkyblockIslandEvent(SkyblockIsland island) {
        this.island = island;
    }

    public SkyblockIsland getIsland() {
        return island;
    }

}
