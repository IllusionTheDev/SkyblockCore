package me.illusion.skyblockcore.bungee;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import me.illusion.skyblockcore.bungee.command.SimpleSkyblockCommand;
import me.illusion.skyblockcore.bungee.config.SkyblockCacheDatabasesFile;
import me.illusion.skyblockcore.bungee.config.SkyblockDatabasesFile;
import me.illusion.skyblockcore.bungee.config.SkyblockMatchmakingFile;
import me.illusion.skyblockcore.bungee.instance.BungeeSkyblockMatchmaker;
import me.illusion.skyblockcore.bungee.profile.BungeeProfileCache;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseRegistry;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManager;
import me.illusion.skyblockcore.common.event.manager.SkyblockEventManagerImpl;
import me.illusion.skyblockcore.common.profile.SkyblockProfileCache;
import me.illusion.skyblockcore.proxy.SkyblockProxyPlatform;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.ServerDataComparator;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.SkyblockServerComparatorRegistry;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.SkyblockServerComparatorRegistryImpl;
import me.illusion.skyblockcore.proxy.matchmaking.data.SkyblockServerMatchmaker;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class SkyblockBungeePlugin extends Plugin implements SkyblockProxyPlatform {

    private SkyblockCacheDatabasesFile cacheDatabasesFile;
    private SkyblockDatabasesFile databasesFile;
    private SkyblockMatchmakingFile matchmakingFile;

    private SkyblockDatabaseRegistry databaseRegistry;
    private SkyblockEventManager eventManager;
    private SkyblockProfileCache profileCache;

    private SkyblockServerMatchmaker matchmaker;
    private SkyblockServerComparatorRegistry matchmakerComparatorRegistry;

    @Override
    public void onEnable() {
        cacheDatabasesFile = new SkyblockCacheDatabasesFile(this);
        databasesFile = new SkyblockDatabasesFile(this);
        matchmakingFile = new SkyblockMatchmakingFile(this);

        eventManager = new SkyblockEventManagerImpl();
        databaseRegistry = new SkyblockDatabaseRegistry(this);

        matchmakerComparatorRegistry = new SkyblockServerComparatorRegistryImpl();

        ProxyServer.getInstance().getScheduler().schedule(this, this::loadDatabase, 1, TimeUnit.SECONDS);
    }

    private void finishEnable() {
        profileCache = new BungeeProfileCache(this);
        matchmaker = new BungeeSkyblockMatchmaker(this);

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SimpleSkyblockCommand(this));
    }

    private void initMatchmaking() {
        String preferredComparator = matchmakingFile.getPreferredComparator();

        ServerDataComparator comparator = matchmakerComparatorRegistry.getComparator(preferredComparator);

        if (comparator == null) {
            getLogger().warning("Preferred comparator '" + preferredComparator + "' not found. Using default comparator.");
            comparator = matchmakerComparatorRegistry.getComparator("least-islands");
        }

        matchmakerComparatorRegistry.setDefaultComparator(comparator);
    }

    private void loadDatabase() {
        databaseRegistry.tryEnableMultiple(cacheDatabasesFile, databasesFile).thenAccept(success -> {
            if (!success) {
                getLogger().severe("Failed to enable databases. Disabling plugin.");
                ProxyServer.getInstance().getPluginManager().unregisterListeners(this);
                ProxyServer.getInstance().getPluginManager().unregisterCommands(this);
                return;
            }

            initMatchmaking();
            finishEnable();
        });
    }

    @Override
    public void onDisable() {
        databaseRegistry.getChosenCacheDatabase().flush().join();
        databaseRegistry.getChosenDatabase().flush().join();
    }
}
