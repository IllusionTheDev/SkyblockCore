package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.cosmos.CosmosPlugin;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.config.impl.SkyblockCacheDatabasesFile;
import me.illusion.skyblockcore.common.config.impl.SkyblockDatabasesFile;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.event.impl.SkyblockPlatformEnabledEvent;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManagerImpl;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.network.SkyblockNetworkRegistry;
import me.illusion.skyblockcore.server.network.SkyblockNetworkRegistryImpl;
import me.illusion.skyblockcore.server.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.network.simple.SimpleSkyblockNetwork;
import me.illusion.skyblockcore.server.player.SkyblockPlayerManager;
import me.illusion.skyblockcore.spigot.command.SkyblockBukkitCommandManager;
import me.illusion.skyblockcore.spigot.config.BukkitConfigurationProvider;
import me.illusion.skyblockcore.spigot.config.cosmos.SkyblockCosmosSetupFile;
import me.illusion.skyblockcore.spigot.cosmos.SkyblockCosmosSetup;
import me.illusion.skyblockcore.spigot.grid.SkyblockGridRegistry;
import me.illusion.skyblockcore.spigot.island.IslandManagerImpl;
import me.illusion.skyblockcore.spigot.player.SkyblockBukkitPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the SkyblockCore Spigot platform plugin.
 */
@Getter
public class SkyblockSpigotPlugin extends JavaPlugin implements SkyblockServerPlatform {

    // Spigot-specific things
    private SkyblockCosmosSetup cosmosSetup;
    private SkyblockGridRegistry gridRegistry;


    // Server-platform specific things
    private SkyblockDatabasesFile databasesFile;
    private SkyblockCacheDatabasesFile cacheDatabasesFile;
    private SkyblockMessagesFile messagesFile;

    private ConfigurationProvider configurationProvider;

    private SkyblockDatabaseRegistry databaseRegistry;
    private SkyblockIslandManager islandManager;
    private SkyblockNetworkRegistry networkRegistry;
    private SkyblockEventManager eventManager;
    private SkyblockPlayerManager playerManager;
    private SkyblockCommandManager<SkyblockAudience> commandManager;

    @Override
    public void onEnable() {
        System.out.println("Loading configuration provider");
        configurationProvider = new BukkitConfigurationProvider(this);

        System.out.println("Loading network registry");
        networkRegistry = new SkyblockNetworkRegistryImpl(this);

        System.out.println("Loading configuration files");
        databasesFile = new SkyblockDatabasesFile(this);
        cacheDatabasesFile = new SkyblockCacheDatabasesFile(this);
        messagesFile = new SkyblockMessagesFile(this, "server-messages");

        System.out.println("Loading database & grid");
        databaseRegistry = new SkyblockDatabaseRegistry(this);
        gridRegistry = new SkyblockGridRegistry();

        System.out.println("Loading events & commands");
        eventManager = new SkyblockEventManagerImpl();
        commandManager = new SkyblockBukkitCommandManager(this);

        System.out.println("Registering networks");
        registerNetworks();

        System.out.println("Finishing loading");
        Bukkit.getScheduler().runTask(this, this::finishLoading);
    }

    @Override
    public void onDisable() {
        SkyblockNetworkStructure network = networkRegistry.getActiveStructure();

        if (network != null) {
            network.disable();
        }

        if (islandManager != null) {
            islandManager.disable(true, false).join();
            islandManager.flush().join();
        }


        databaseRegistry.getChosenDatabase().flush().join();
        databaseRegistry.getChosenCacheDatabase().flush().join();
    }

    private void finishLoading() {
        System.out.println("Loading networks");
        networkRegistry.load();

        System.out.println("Initializing cosmos");
        initCosmos();

        System.out.println("Enabling databases");
        databaseRegistry.tryEnableMultiple(databasesFile, cacheDatabasesFile).thenAccept(success -> {
            if (Boolean.FALSE.equals(success)) { // The future returns a boxed boolean
                getLogger().severe("Failed to enable databases, disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            System.out.println("Enabling island manager");
            playerManager = new SkyblockBukkitPlayerManager(this);
            islandManager = new IslandManagerImpl(this);

            networkRegistry.enable();

            commandManager.syncCommands();
            eventManager.callEvent(new SkyblockPlatformEnabledEvent(this));
        }).exceptionally(throwable -> {
            getLogger().severe("Failed to enable databases, disabling plugin...");
            throwable.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return null;
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

        cosmosPlugin.getGridRegistry().register(cosmosSetup.getIslandGrid());
        cosmosPlugin.getSessionHolderRegistry().registerHolder("skyblock", cosmosSetup.getSessionHolder());
    }

    @Override
    public void disableExceptionally() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
