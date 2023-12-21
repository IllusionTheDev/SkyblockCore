package me.illusion.skyblockcore.bungee;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.Getter;
import me.illusion.skyblockcore.bungee.command.BungeeSkyblockCommandManager;
import me.illusion.skyblockcore.bungee.config.BungeeConfigurationProvider;
import me.illusion.skyblockcore.bungee.instance.BungeeSkyblockMatchmaker;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.manager.SkyblockCommandManager;
import me.illusion.skyblockcore.common.config.SkyblockMessagesFile;
import me.illusion.skyblockcore.common.database.registry.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManagerImpl;
import me.illusion.skyblockcore.common.registry.Registries;
import me.illusion.skyblockcore.common.scheduler.SkyblockScheduler;
import me.illusion.skyblockcore.common.utilities.file.IOUtils;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;
import me.illusion.skyblockcore.proxy.command.PlaySkyblockCommand;
import me.illusion.skyblockcore.proxy.config.SkyblockMatchmakingFile;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.SkyblockServerComparatorRegistry;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.SkyblockServerComparatorRegistryImpl;
import me.illusion.skyblockcore.proxy.matchmaking.data.SkyblockServerMatchmaker;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * Bungee implementation of {@link SkyblockProxyPlatform}.
 */
@Getter
public class SkyblockBungeePlugin extends Plugin implements SkyblockProxyPlatform {

    private SkyblockMatchmakingFile matchmakingFile;

    private SkyblockDatabaseRegistry databaseRegistry;
    private SkyblockEventManager eventManager;
    private SkyblockScheduler scheduler;

    private SkyblockServerMatchmaker matchmaker;
    private SkyblockServerComparatorRegistry matchmakerComparatorRegistry;

    private BungeeConfigurationProvider configurationProvider;

    private SkyblockCommandManager<SkyblockAudience> commandManager;
    private SkyblockMessagesFile messagesFile;

    private Registries registries;

    @Override
    public void onEnable() {
        registries = new Registries();
        configurationProvider = new BungeeConfigurationProvider(this);

        commandManager = new BungeeSkyblockCommandManager(this);

        matchmakingFile = new SkyblockMatchmakingFile(this);
        messagesFile = new SkyblockMessagesFile(this, "proxy-messages");

        eventManager = new SkyblockEventManagerImpl();
        databaseRegistry = new SkyblockDatabaseRegistry(this);

        matchmakerComparatorRegistry = new SkyblockServerComparatorRegistryImpl();

        new PlaySkyblockCommand(this);

        ProxyServer.getInstance().getScheduler().schedule(this, this::loadDatabase, 1, TimeUnit.SECONDS);
    }

    private void finishEnable() {
        matchmaker = new BungeeSkyblockMatchmaker(this);
    }

    private void initMatchmaking() {
        String preferredComparator = matchmakingFile.getPreferredComparator();

        ServerDataComparator comparator = matchmakerComparatorRegistry.getComparator(preferredComparator);

        if (comparator == null) {
            getLogger().log(Level.WARNING, "Preferred comparator {0} not found. Using default comparator.", preferredComparator);
            comparator = matchmakerComparatorRegistry.getComparator("least-islands");
        }

        matchmakerComparatorRegistry.setDefaultComparator(comparator);
    }

    private void loadDatabase() {
        File databasesFolder = new File(getDataFolder(), "databases");

        IOUtils.traverseAndLoad(databasesFolder, file -> {
            if (!file.getName().endsWith(".yml")) {
                return;
            }

            databaseRegistry.loadPossible(configurationProvider.loadConfiguration(file));
        });

        databaseRegistry.finishLoading().thenRun(() -> {
            finishEnable();
            initMatchmaking();
        });
    }

    @Override
    public void onDisable() {
        databaseRegistry.shutdown().join();
    }

    @Override
    public void disableExceptionally() {
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

        pluginManager.unregisterListeners(this);
        pluginManager.unregisterCommands(this);
    }
}
