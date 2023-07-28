package me.illusion.skyblockcore.server.event.island;

import me.illusion.skyblockcore.server.island.SkyblockIsland;

/**
 * This event is called when a SkyblockIsland is unloaded.
 */
public class SkyblockIslandUnloadEvent extends SkyblockIslandEvent {

    public SkyblockIslandUnloadEvent(SkyblockIsland island) {
        super(island);
    }

}
