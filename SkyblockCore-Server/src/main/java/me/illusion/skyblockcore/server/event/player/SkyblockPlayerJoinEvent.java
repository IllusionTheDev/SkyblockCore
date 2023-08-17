package me.illusion.skyblockcore.server.event.player;

import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * This event is called when a player joins the server. Contains the player and their chosen profile ID. This event may be called multiple times for the same
 * player if they switch profiles.
 */
public class SkyblockPlayerJoinEvent extends SkyblockPlayerEvent {

    public SkyblockPlayerJoinEvent(SkyblockPlayer player) {
        super(player);
    }

}
