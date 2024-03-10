package me.illusion.skyblockcore.spigot.grid;

import me.illusion.cosmos.grid.CosmosGrid;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;

public interface SkyblockGridProvider { // TODO: Move this entire package to Cosmos

    /**
     * Provides a grid for the given section
     *
     * @param section the section
     * @return the grid
     */
    CosmosGrid provide(ConfigurationSection section);

}
