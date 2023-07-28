package me.illusion.skyblockcore.spigot.event.island;

import me.illusion.skyblockcore.server.island.SkyblockIsland;
import me.illusion.skyblockcore.spigot.event.SkyblockEvent;

public abstract class SkyblockIslandEvent extends SkyblockEvent {

    private final SkyblockIsland island;

    public SkyblockIslandEvent(SkyblockIsland island) {
        this.island = island;
    }

    public SkyblockIsland getIsland() {
        return island;
    }

}
