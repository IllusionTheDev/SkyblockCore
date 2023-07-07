package me.illusion.skyblockcore.spigot.cosmos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.illusion.cosmos.CosmosPlugin;
import me.illusion.cosmos.cache.CosmosCache;
import me.illusion.cosmos.database.CosmosDataContainer;
import me.illusion.cosmos.session.CosmosSessionHolder;
import me.illusion.cosmos.template.TemplatedArea;
import me.illusion.cosmos.template.grid.CosmosGrid;

@Getter
@AllArgsConstructor
public class SkyblockCosmosSetup {

    private final CosmosDataContainer islandContainer;
    private final CosmosGrid islandGrid;
    private final CosmosSessionHolder sessionHolder;
    private final CosmosPlugin cosmosPlugin;
    private final CosmosCache<TemplatedArea> templateCache;


}
