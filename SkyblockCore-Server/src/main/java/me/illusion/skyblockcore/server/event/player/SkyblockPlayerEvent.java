package me.illusion.skyblockcore.server.event.player;

import java.util.UUID;
import me.illusion.skyblockcore.common.event.SkyblockEvent;

/**
 * Generic Skyblock player event. Contains the player and their chosen profile ID.
 */
public abstract class SkyblockPlayerEvent extends SkyblockEvent {

    private final UUID playerId;
    private final UUID profileId;

    public SkyblockPlayerEvent(UUID playerId, UUID profileId) {
        this.playerId = playerId;
        this.profileId = profileId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getProfileId() {
        return profileId;
    }
}
