package me.illusion.skyblockcore.spigot.config.cosmos;

import me.illusion.cosmos.CosmosPlugin;
import me.illusion.cosmos.database.CosmosContainerRegistry;
import me.illusion.cosmos.database.CosmosDataContainer;
import me.illusion.cosmos.grid.CosmosGrid;
import me.illusion.cosmos.grid.impl.WorldPerAreaGrid;
import me.illusion.cosmos.session.CosmosSessionHolder;
import me.illusion.cosmos.utilities.storage.YMLBase;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SkyblockCosmosSetupFile extends YMLBase {

    private final SkyblockCosmosSetup setup;

    private final CosmosPlugin cosmosPlugin;
    private final SkyblockSpigotPlugin skyblockPlugin;

    public SkyblockCosmosSetupFile(CosmosPlugin plugin, SkyblockSpigotPlugin skyblockPlugin) {
        super(skyblockPlugin, "cosmos-setup.yml");

        this.cosmosPlugin = plugin;
        this.skyblockPlugin = skyblockPlugin;

        CosmosDataContainer container = setupContainer();
        CosmosGrid grid = setupGrid();

        CosmosSessionHolder sessionHolder = new CosmosSessionHolder(plugin, container, grid);

        setup = new SkyblockCosmosSetup(container, grid, sessionHolder, plugin, plugin.getTemplateCache());
    }

    private CosmosDataContainer setupContainer() {
        FileConfiguration configuration = getConfiguration();

        String preferredContainer = configuration.getString("preferred-container");
        CosmosContainerRegistry registry = cosmosPlugin.getContainerRegistry();

        CosmosDataContainer container = registry.getContainer(preferredContainer);

        if (container == null) {
            container = registry.getDefaultContainer();
        }

        return container;
    }

    private CosmosGrid setupGrid() {
        FileConfiguration configuration = getConfiguration();

        String preferredGrid = configuration.getString("preferred-grid", "world-per-area");
        ConfigurationSection section = configuration.getConfigurationSection(preferredGrid);

        if (section == null) {
            return new WorldPerAreaGrid();
        }

        return skyblockPlugin.getGridRegistry().getProvider(preferredGrid).provide(section);
    }

    public SkyblockCosmosSetup getSetup() {
        return setup;
    }
}
