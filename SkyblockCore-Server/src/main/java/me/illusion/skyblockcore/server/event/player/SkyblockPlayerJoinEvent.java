package me.illusion.skyblockcore.server.event.player;

import java.util.UUID;

/**
 * This event is called when a player joins the server. Contains the player and their chosen profile ID. This event may be called multiple times for the same
 * player if they switch profiles.
 */
public class SkyblockPlayerJoinEvent extends SkyblockPlayerEvent {

    public SkyblockPlayerJoinEvent(UUID playerId, UUID profileId) {
        super(playerId, profileId);
    }

}
