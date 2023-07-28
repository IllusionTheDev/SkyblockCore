package me.illusion.skyblockcore.server.event.player;

import java.util.UUID;

/**
 * This event is called when a player quits the server. Contains the player and their chosen profile ID. This event may be called multiple times for the same
 * player if they switch profiles.
 */
public class SkyblockPlayerQuitEvent extends SkyblockPlayerEvent {

    public SkyblockPlayerQuitEvent(UUID playerId, UUID profileId) {
        super(playerId, profileId);
    }

}
