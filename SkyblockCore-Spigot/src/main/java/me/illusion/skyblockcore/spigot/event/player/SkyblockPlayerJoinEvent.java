package me.illusion.skyblockcore.spigot.event.player;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called when a player joins the server. Contains the player and their chosen profile ID. This event may be called multiple times for the same
 * player if they switch profiles.
 */
public class SkyblockPlayerJoinEvent extends SkyblockPlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public SkyblockPlayerJoinEvent(Player player, UUID profileId) {
        super(player, profileId);
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
