package me.illusion.skyblockcore.server.player;

import java.util.UUID;
import me.illusion.skyblockcore.server.util.SkyblockLocation;

/**
 * Represents a Skyblock player on the server.
 */
public interface SkyblockPlayer {

    /**
     * Obtains the name of the player.
     *
     * @return The name of the player.
     */
    String getName();

    /**
     * Obtains the unique id of the player.
     *
     * @return The unique id of the player.
     */
    UUID getUniqueId();

    /**
     * Obtains the selected profile id of the player.
     *
     * @return The selected profile id of the player.
     */
    UUID getSelectedProfileId();

    /**
     * Obtains the location of the player.
     *
     * @return The location of the player.
     */
    SkyblockLocation getLocation();

    /**
     * Teleports the player to the given location.
     *
     * @param location The location to teleport the player to.
     */
    void teleport(SkyblockLocation location);

}
