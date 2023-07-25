package me.illusion.skyblockcore.spigot.event.startup;

import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.event.SkyblockEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SkyblockEnabledEvent extends SkyblockEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final SkyblockSpigotPlugin plugin;

    public SkyblockEnabledEvent(SkyblockSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public SkyblockSpigotPlugin getPlugin() {
        return plugin;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
