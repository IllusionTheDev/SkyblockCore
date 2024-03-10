package me.illusion.skyblockcore.spigot;

import java.io.File;
import java.util.logging.Level;
import lombok.Getter;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.database.registry.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.event.impl.SkyblockPlatformEnabledEvent;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManagerImpl;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.platform.SkyblockPlatformProvider;
import me.illusion.skyblockcore.common.registry.Registries;
import me.illusion.skyblockcore.common.scheduler.SkyblockScheduler;
import me.illusion.skyblockcore.common.utilities.file.IOUtils;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;
import me.illusion.skyblockcore.server.config.IslandManagerConfiguration;
import me.illusion.skyblockcore.server.inventory.platform.SkyblockInventoryFactory;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.island.provider.SkyblockIslandManagerProviderRegistry;
import me.illusion.skyblockcore.server.network.SkyblockNetworkRegistry;
import me.illusion.skyblockcore.server.network.SkyblockNetworkRegistryImpl;
import me.illusion.skyblockcore.server.network.SkyblockNetworkStructure;
import me.illusion.skyblockcore.server.network.complex.ComplexSkyblockNetwork;
import me.illusion.skyblockcore.server.network.simple.SimpleSkyblockNetwork;
import me.illusion.skyblockcore.server.player.SkyblockPlayerManager;
import me.illusion.skyblockcore.spigot.command.SkyblockBukkitCommandManager;
import me.illusion.skyblockcore.spigot.config.BukkitConfigurationProvider;
import me.illusion.skyblockcore.spigot.grid.SkyblockGridRegistry;
import me.illusion.skyblockcore.spigot.inventory.BukkitInventoryFactory;
import me.illusion.skyblockcore.spigot.inventory.BukkitInventoryTracker;
import me.illusion.skyblockcore.spigot.island.PluginIslandManagerProvider;
import me.illusion.skyblockcore.spigot.island.cosmos.CosmosIslandManager;
import me.illusion.skyblockcore.spigot.island.slime.SWMIslandManager;
import me.illusion.skyblockcore.spigot.player.SkyblockBukkitPlayerManager;
import me.illusion.skyblockcore.spigot.registries.BukkitMaterialRegistry;
import me.illusion.skyblockcore.spigot.scheduler.SkyblockBukkitScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the SkyblockCore Spigot platform plugin.
 */
@Getter
public class SkyblockSpigotPlugin extends JavaPlugin implements SkyblockServerPlatform {

    // Spigot-specific things
    private SkyblockGridRegistry cosmosGridRegistry;

    // Server-platform specific things
    private SkyblockMessagesFile messagesFile;
    private SkyblockInventoryFactory inventoryFactory;
    private IslandManagerConfiguration islandManagerConfiguration;
    private SkyblockIslandManagerProviderRegistry islandManagerProviders;

    private ConfigurationProvider configurationProvider;

    private Registries registries;
    private SkyblockDatabaseRegistry databaseRegistry;
    private SkyblockIslandManager islandManager;
    private SkyblockNetworkRegistry networkRegistry;
    private SkyblockEventManager eventManager;
    private SkyblockPlayerManager playerManager;
    private SkyblockScheduler scheduler;
    private SkyblockCommandManager<SkyblockAudience> commandManager;


    @Override
    public void onLoad() {
        Bukkit.getServicesManager().register(SkyblockPlatform.class, this, this, ServicePriority.Normal);
        SkyblockPlatformProvider.setPlatform(this);
    }

    @Override
    public void onEnable() {
        log("Loading configuration provider");
        configurationProvider = new BukkitConfigurationProvider(this);

        log("Loading scheduler");
        scheduler = new SkyblockBukkitScheduler(this);

        log("Loading providers");
        inventoryFactory = new BukkitInventoryFactory(this);
        islandManagerProviders = new SkyblockIslandManagerProviderRegistry(this);
        cosmosGridRegistry = new SkyblockGridRegistry();

        registerDefaultProviders();

        log("Loading minecraft registries..");
        loadRegistries();

        log("Loading network registry");
        networkRegistry = new SkyblockNetworkRegistryImpl(this);

        log("Loading configuration files");
        messagesFile = new SkyblockMessagesFile(this, "server-messages");
        islandManagerConfiguration = new IslandManagerConfiguration(this);

        log("Loading database & grid");
        databaseRegistry = new SkyblockDatabaseRegistry(this);

        log("Loading events & commands");
        eventManager = new SkyblockEventManagerImpl();
        commandManager = new SkyblockBukkitCommandManager(this);

        log("Registering networks");
        registerNetworks();

        log("Finishing loading");
        Bukkit.getScheduler().runTask(this, this::load);
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

        databaseRegistry.shutdown().join();
    }

    private void load() {
        log("Loading networks");
        networkRegistry.load();


        log("Enabling databases");
        loadDatabases();
        databaseRegistry.finishLoading().thenAccept(this::finishLoading);
    }

    private void loadDatabases() {
        File databasesFolder = new File(getDataFolder(), "databases");

        IOUtils.copyFolder(this, getFile(), databasesFolder.getName());

        IOUtils.traverseAndLoad(databasesFolder, file -> {
            if (!file.getName().endsWith(".yml")) {
                return;
            }

            databaseRegistry.loadPossible(configurationProvider.loadConfiguration(file));
        });
    }

    private void finishLoading(boolean databasesLoaded) {
        if (!databasesLoaded) {
            getLogger().severe("Failed to enable databases, disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        log("Enabling island manager");
        playerManager = new SkyblockBukkitPlayerManager(this);
        islandManager = islandManagerProviders.tryProvide();

        if (islandManager == null) {
            getLogger().severe("Failed to enable island manager, disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        new BukkitInventoryTracker(this);

        networkRegistry.enable();

        commandManager.syncCommands();
        eventManager.callEvent(new SkyblockPlatformEnabledEvent(this));
    }

    /**
     * Registers the default skyblock networks
     */
    private void registerNetworks() {
        networkRegistry.register(new ComplexSkyblockNetwork(this));
        networkRegistry.register(new SimpleSkyblockNetwork(this));
    }

    private void registerDefaultProviders() {
        islandManagerProviders.registerProvider("cosmos", PluginIslandManagerProvider.of(section -> new CosmosIslandManager(section, this), "Cosmos"));
        islandManagerProviders.registerProvider("slime", PluginIslandManagerProvider.of(section -> new SWMIslandManager(section, this), "SlimeWorldManager"));
    }

    private void loadRegistries() {
        registries = new Registries();

        registries.registerRegistry(new BukkitMaterialRegistry());
    }

    public BukkitMaterialRegistry getMaterialRegistry() {
        return registries.getSpecificRegistry(BukkitMaterialRegistry.class);
    }

    @Override
    public void disableExceptionally() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
    
    private void log(String message, Object... objects) {
        getLogger().log(Level.INFO, message, objects);
    }
}
