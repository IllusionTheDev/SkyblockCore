package me.illusion.skyblockcore.server.event.player;

import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * This event is called when a player quits the server. Contains the player and their chosen profile ID. This event may be called multiple times for the same
 * player if they switch profiles.
 */
public class SkyblockPlayerQuitEvent extends SkyblockPlayerEvent {

    public SkyblockPlayerQuitEvent(SkyblockPlayer player) {
        super(player);
    }

}
