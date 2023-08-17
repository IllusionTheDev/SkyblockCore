package me.illusion.skyblockcore.server.event.player;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.server.player.SkyblockPlayer;

/**
 * Generic Skyblock player event. Contains the player and their chosen profile ID.
 */
public abstract class SkyblockPlayerEvent extends SkyblockEvent {

    private final SkyblockPlayer player;

    protected SkyblockPlayerEvent(SkyblockPlayer player) {
        this.player = player;
    }

    public SkyblockPlayer getPlayer() {
        return player;
    }
}
