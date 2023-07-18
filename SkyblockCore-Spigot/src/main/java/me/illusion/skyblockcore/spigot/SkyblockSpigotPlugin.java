package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.cosmos.CosmosPlugin;
import me.illusion.cosmos.cache.CosmosCache;
import me.illusion.cosmos.database.CosmosDataContainer;
import me.illusion.cosmos.pool.world.WorldPoolSettings;
import me.illusion.cosmos.session.CosmosSessionHolder;
import me.illusion.cosmos.template.TemplatedArea;
import me.illusion.cosmos.template.grid.CosmosGrid;
import me.illusion.cosmos.template.grid.impl.WorldPerAreaGrid;
import me.illusion.cosmos.utilities.command.command.CommandManager;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkRegistry;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SkyblockSpigotPlugin extends JavaPlugin {

    // General things (usually present in Cosmos)
    private CommandManager commandManager;
    private MessagesFile messages;

    // Skyblock-specific setup
    private SkyblockCosmosSetup cosmosSetup;
    private SkyblockDatabase database;

    private IslandManager islandManager;

    private SkyblockNetworkRegistry networkRegistry;

    @Override
    public void onEnable() {
        messages = new MessagesFile(this);
        commandManager = new CommandManager(this, messages);

        networkRegistry = new SkyblockNetworkRegistry(this);

        initCosmos();
        initDatabase();

        islandManager = new IslandManager(this);

        registerNetworks();
    }

    @Override
    public void onDisable() {
        SkyblockNetworkStructure network = networkRegistry.getActiveStructure();

        if (network != null) {
            network.disable();
        }

        islandManager.disable(true, false);
    }

    private void registerNetworks() {
        networkRegistry.register(new ComplexSkyblockNetwork(this));
        networkRegistry.register(new SimpleSkyblockNetwork(this));
    }

    private void initCosmos() {
        // We get the cosmos plugin
        CosmosPlugin cosmos = (CosmosPlugin) getServer().getPluginManager().getPlugin("Cosmos");

        // We get the default container, this is set in the cosmos config
        CosmosDataContainer container = cosmos.getContainerRegistry().getDefaultContainer();

        // We create a world per area grid, this might be configurable later
        CosmosGrid grid = new WorldPerAreaGrid(
            WorldPoolSettings.builder().build() // Default world pool
        );

        // Create a session holder for skyblock, this is where sessions will be stored
        CosmosSessionHolder sessionHolder = new CosmosSessionHolder(this, container, grid);

        // We get the template cache, this is where templates will be cached
        CosmosCache<TemplatedArea> cache = cosmos.getTemplateCache();

        cosmosSetup = new SkyblockCosmosSetup(
            container,
            grid,
            sessionHolder,
            cosmos,
            cache
        );
    }

    private void initDatabase() {
        // TODO
    }

}
