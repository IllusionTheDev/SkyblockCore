package me.illusion.skyblockcore.server.player;

import java.util.UUID;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.server.inventory.NamedMenu;
import me.illusion.skyblockcore.server.inventory.PlayerItemContainer;
import me.illusion.skyblockcore.server.util.SkyblockLocation;

/**
 * Represents a Skyblock player on the server.
 */
public interface SkyblockPlayer extends SkyblockAudience {

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
     * Obtains the selected profile id of the player. This id is should be used when dealing with
     * skyblock player data.
     *
     * @return The selected profile id of the player, or NULL if the profile is not loaded.
     * (If this returns null, you have a memory leak.)
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

    PlayerItemContainer getInventory();

    default void openMenu(NamedMenu menu) {
        menu.open(this);
    }

}
