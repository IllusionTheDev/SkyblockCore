package me.illusion.skyblockcore.spigot.event.island;

import me.illusion.skyblockcore.spigot.event.SkyblockEvent;
import me.illusion.skyblockcore.spigot.island.Island;

public class SkyblockIslandEvent extends SkyblockEvent {

    private final Island island;

    public SkyblockIslandEvent(Island island) {
        this.island = island;
    }

    public Island getIsland() {
        return island;
    }

}
