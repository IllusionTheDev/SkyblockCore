package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.cosmos.CosmosPlugin;
import me.illusion.cosmos.cache.CosmosCache;
import me.illusion.cosmos.database.CosmosDataContainer;
import me.illusion.cosmos.grid.CosmosGrid;
import me.illusion.cosmos.grid.impl.WorldPerAreaGrid;
import me.illusion.cosmos.session.CosmosSessionHolder;
import me.illusion.cosmos.template.TemplatedArea;
import me.illusion.cosmos.utilities.command.command.CommandManager;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.cosmos.world.pool.WorldPoolSettings;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import me.illusion.skyblockcore.spigot.database.SkyblockDatabasesFile;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkRegistry;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SkyblockSpigotPlugin extends JavaPlugin implements SkyblockPlatform {

    // General things (usually present in Cosmos)
    private CommandManager commandManager;
    private MessagesFile messages;

    // Skyblock-specific setup
    private SkyblockCosmosSetup cosmosSetup;

    private SkyblockDatabasesFile databasesFile;
    private SkyblockDatabaseRegistry databaseRegistry;

    private IslandManager islandManager;

    private SkyblockNetworkRegistry networkRegistry;

    @Override
    public void onEnable() {
        messages = new MessagesFile(this);
        commandManager = new CommandManager(this, messages);

        networkRegistry = new SkyblockNetworkRegistry(this);
        databasesFile = new SkyblockDatabasesFile(this);
        databaseRegistry = new SkyblockDatabaseRegistry(this);

        initCosmos();

        islandManager = new IslandManager(this);

        registerNetworks();

        Bukkit.getScheduler().runTask(this, this::finishLoading);
    }

    @Override
    public void onDisable() {
        SkyblockNetworkStructure network = networkRegistry.getActiveStructure();

        if (network != null) {
            network.disable();
        }

        islandManager.disable(true, false);
    }

    private void finishLoading() {
        networkRegistry.load();
        databaseRegistry.tryEnable(databasesFile).thenAccept(success -> {
            if (!success) {
                getLogger().severe("Failed to enable databases, disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(this);
            }

            networkRegistry.enable();
        });
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

}
