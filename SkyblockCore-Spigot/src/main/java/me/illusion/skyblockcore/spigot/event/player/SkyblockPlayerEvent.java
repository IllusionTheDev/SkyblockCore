package me.illusion.skyblockcore.spigot.event.player;

import java.util.UUID;
import me.illusion.skyblockcore.spigot.event.SkyblockEvent;
import org.bukkit.entity.Player;

/**
 * Generic Skyblock player event. Contains the player and their chosen profile ID.
 */
public abstract class SkyblockPlayerEvent extends SkyblockEvent {

    private final Player player;
    private final UUID profileId;

    public SkyblockPlayerEvent(Player player, UUID profileId) {
        this.player = player;
        this.profileId = profileId;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getProfileId() {
        return profileId;
    }
}
