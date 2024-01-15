package me.illusion.skyblockcore.spigot.config.cosmos;

import me.illusion.cosmos.CosmosPlugin;
import me.illusion.cosmos.database.CosmosContainerRegistry;
import me.illusion.cosmos.database.CosmosDataContainer;
import me.illusion.cosmos.grid.CosmosGrid;
import me.illusion.cosmos.grid.impl.WorldPerAreaGrid;
import me.illusion.cosmos.session.CosmosSessionHolder;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.spigot.SkyblockSpigotPlugin;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;

/**
 * Represents the cosmos setup file. This is used to load the cosmos setup.
 */
public class SkyblockCosmosSetupFile {

    private final ConfigurationSection configuration;
    private final SkyblockCosmosSetup setup;

    private final CosmosPlugin cosmosPlugin;
    private final SkyblockSpigotPlugin skyblockPlugin;

    public SkyblockCosmosSetupFile(ConfigurationSection section, CosmosPlugin plugin, SkyblockSpigotPlugin skyblockPlugin) {
        this.configuration = section;
        this.cosmosPlugin = plugin;
        this.skyblockPlugin = skyblockPlugin;

        CosmosDataContainer container = setupContainer();
        CosmosGrid grid = setupGrid();

        CosmosSessionHolder sessionHolder = new CosmosSessionHolder(plugin, container, grid);

        setup = new SkyblockCosmosSetup(container, grid, sessionHolder, plugin, plugin.getTemplateCache());
    }

    private CosmosDataContainer setupContainer() {
        String preferredContainer = configuration.getString("preferred-container");
        CosmosContainerRegistry registry = cosmosPlugin.getContainerRegistry();

        CosmosDataContainer container = registry.getContainer(preferredContainer);

        if (container == null) {
            container = registry.getDefaultContainer();
        }

        return container;
    }

    private CosmosGrid setupGrid() {
        String preferredGrid = configuration.getString("preferred-grid", "world-per-area");
        ConfigurationSection section = configuration.getSection(preferredGrid);

        if (section == null) {
            return new WorldPerAreaGrid();
        }

        return skyblockPlugin.getCosmosGridRegistry().getProvider(preferredGrid).provide(section);
    }

    public SkyblockCosmosSetup getSetup() {
        return setup;
    }
}
