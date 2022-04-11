package me.illusion.skyblockcore.spigot.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class IslandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public IslandEvent() {
        super(!Bukkit.isPrimaryThread());
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
