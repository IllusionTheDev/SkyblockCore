package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.cosmos.CosmosPlugin;
import me.illusion.cosmos.utilities.command.command.CommandManager;
import me.illusion.cosmos.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.event.impl.SkyblockPlatformEnabledEvent;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManagerImpl;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.SkyblockNetworkRegistry;
import me.illusion.skyblockcore.server.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.server.player.SkyblockPlayerManager;
import me.illusion.skyblockcore.spigot.config.BukkitConfigurationProvider;
import me.illusion.skyblockcore.spigot.config.SkyblockCacheDatabasesFile;
import me.illusion.skyblockcore.spigot.config.SkyblockDatabasesFile;
import me.illusion.skyblockcore.spigot.config.cosmos.SkyblockCosmosSetupFile;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import me.illusion.skyblockcore.spigot.grid.SkyblockGridRegistry;
import me.illusion.skyblockcore.spigot.island.IslandManagerImpl;
import me.illusion.skyblockcore.spigot.network.SkyblockNetworkRegistryImpl;
import me.illusion.skyblockcore.spigot.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.spigot.network.simple.SimpleSkyblockNetwork;
import me.illusion.skyblockcore.spigot.player.SkyblockBukkitPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the SkyblockCore Spigot platform plugin.
 */
@Getter
public class SkyblockSpigotPlugin extends JavaPlugin implements SkyblockServerPlatform {

    // Non-platform spigot specific things
    private CommandManager commandManager;
    private MessagesFile messages;

    // Spigot-specific things
    private SkyblockCosmosSetup cosmosSetup;
    private SkyblockGridRegistry gridRegistry;

    private SkyblockDatabasesFile databasesFile;
    private SkyblockCacheDatabasesFile cacheDatabasesFile;

    // Server-platform specific things
    private ConfigurationProvider configurationProvider;

    private SkyblockDatabaseRegistry databaseRegistry;
    private SkyblockIslandManager islandManager;
    private SkyblockNetworkRegistry networkRegistry;
    private SkyblockEventManager eventManager;
    private SkyblockPlayerManager playerManager;

    @Override
    public void onEnable() {
        messages = new MessagesFile(this);
        commandManager = new CommandManager(this, messages);

        networkRegistry = new SkyblockNetworkRegistryImpl(this);

        databasesFile = new SkyblockDatabasesFile(this);
        cacheDatabasesFile = new SkyblockCacheDatabasesFile(this);
        databaseRegistry = new SkyblockDatabaseRegistry(this);

        gridRegistry = new SkyblockGridRegistry();

        configurationProvider = new BukkitConfigurationProvider(this);
        eventManager = new SkyblockEventManagerImpl();

        registerNetworks();

        Bukkit.getScheduler().runTask(this, this::finishLoading);
    }

    @Override
    public void onDisable() {
        SkyblockNetworkStructure network = networkRegistry.getActiveStructure();

        if (network != null) {
            network.disable();
        }

        islandManager.disable(true, false).join();
        islandManager.flush().join();

        databaseRegistry.getChosenDatabase().flush().join();
        databaseRegistry.getChosenCacheDatabase().flush().join();
    }

    private void finishLoading() {
        networkRegistry.load();
        initCosmos();

        databaseRegistry.tryEnableMultiple(databasesFile, cacheDatabasesFile).thenAccept(success -> {
            if (Boolean.FALSE.equals(success)) { // The future returns a boxed boolean
                getLogger().severe("Failed to enable databases, disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(this);
            }

            playerManager = new SkyblockBukkitPlayerManager(this);
            islandManager = new IslandManagerImpl(this);

            networkRegistry.enable();

            eventManager.callEvent(new SkyblockPlatformEnabledEvent(this));
        });

    }

    /**
     * Registers the default skyblock networks
     */
    private void registerNetworks() {
        networkRegistry.register(new ComplexSkyblockNetwork(this));
        networkRegistry.register(new SimpleSkyblockNetwork(this));
    }

    /**
     * Initializes the cosmos setup
     */
    private void initCosmos() {
        if (cosmosSetup != null) {
            throw new IllegalStateException("Cosmos setup already initialized!");

        }
        // We get the cosmos plugin
        CosmosPlugin cosmosPlugin = (CosmosPlugin) Bukkit.getPluginManager().getPlugin("Cosmos");

        SkyblockCosmosSetupFile cosmosSetupFile = new SkyblockCosmosSetupFile(cosmosPlugin, this);
        cosmosSetup = cosmosSetupFile.getSetup();
    }

}
