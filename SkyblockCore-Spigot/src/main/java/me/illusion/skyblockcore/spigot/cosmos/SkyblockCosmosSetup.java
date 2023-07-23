package me.illusion.skyblockcore.spigot.cosmos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.illusion.cosmos.CosmosPlugin;
import me.illusion.cosmos.cache.CosmosCache;
import me.illusion.cosmos.database.CosmosDataContainer;
import me.illusion.cosmos.grid.CosmosGrid;
import me.illusion.cosmos.session.CosmosSessionHolder;
import me.illusion.cosmos.template.TemplatedArea;

/**
 * This class is used to store all the Cosmos-related data that is used by the Skyblock plugin. Cosmos is the core of the Skyblock plugin, and is used to paste
 * islands, manage sessions, and more.
 */
@Getter
@AllArgsConstructor
public class SkyblockCosmosSetup {

    private final CosmosDataContainer islandContainer; // The container that stores all the island contents
    private final CosmosGrid islandGrid; // The grid where islands are pasted
    private final CosmosSessionHolder sessionHolder; // The session holder that manages sessions
    private final CosmosPlugin cosmosPlugin; // The Cosmos plugin instance
    private final CosmosCache<TemplatedArea> templateCache; // The cache that caches all the island templates, where we will then create new islands from


}
