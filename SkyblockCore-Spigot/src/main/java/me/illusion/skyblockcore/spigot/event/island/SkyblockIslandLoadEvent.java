package me.illusion.skyblockcore.spigot.event.island;

import me.illusion.skyblockcore.spigot.island.Island;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyblockIslandLoadEvent extends SkyblockIslandEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public SkyblockIslandLoadEvent(Island island) {
        super(island);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
