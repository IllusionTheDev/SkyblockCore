package me.illusion.skyblockcore.server;

import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.server.config.IslandManagerConfiguration;
import me.illusion.skyblockcore.server.inventory.platform.SkyblockInventoryFactory;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.player.SkyblockPlayerManager;

/**
 * Represents the Skyblock server platform, which handles the instance logic.
 */
public interface SkyblockServerPlatform extends SkyblockPlatform {

    /**
     * Obtains the island manager, which loads and saves islands.
     *
     * @return The island manager.
     */
    SkyblockIslandManager getIslandManager();

    /**
     * Obtains the player manager, which tracks player and profile data.
     *
     * @return The player manager.
     */
    SkyblockPlayerManager getPlayerManager();

    SkyblockInventoryFactory getInventoryFactory();

    IslandManagerConfiguration getIslandManagerConfiguration();
}
