package me.illusion.skyblockcore.server.player;

import java.util.UUID;

public abstract class AbstractSkyblockPlayer implements SkyblockPlayer {

    protected final SkyblockPlayerManager playerManager;
    protected final UUID playerId;

    public AbstractSkyblockPlayer(SkyblockPlayerManager playerManager, UUID playerId) {
        this.playerManager = playerManager;
        this.playerId = playerId;
    }

    @Override
    public UUID getUniqueId() {
        return playerId;
    }

    @Override
    public UUID getSelectedProfileId() {
        return playerManager.getCachedProfileId(playerId); // This will return null if the player leaves, only happens during a memory leak, so it's fine.
    }
}
