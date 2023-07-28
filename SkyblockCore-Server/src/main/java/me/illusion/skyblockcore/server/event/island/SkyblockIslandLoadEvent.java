package me.illusion.skyblockcore.server.event.island;

import me.illusion.skyblockcore.server.island.SkyblockIsland;

/**
 * This event is called when a SkyblockIsland is loaded.
 */
public class SkyblockIslandLoadEvent extends SkyblockIslandEvent {

    public SkyblockIslandLoadEvent(SkyblockIsland island) {
        super(island);
    }

}
