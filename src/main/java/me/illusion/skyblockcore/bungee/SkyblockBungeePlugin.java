package me.illusion.skyblockcore.bungee;

import lombok.Getter;
import me.illusion.skyblockcore.bungee.command.SkyblockCommand;
import me.illusion.skyblockcore.bungee.data.PlayerFinder;
import me.illusion.skyblockcore.bungee.handler.MessagePacketHandler;
import me.illusion.skyblockcore.bungee.listener.ConnectListener;
import me.illusion.skyblockcore.bungee.listener.RedisListener;
import me.illusion.skyblockcore.bungee.listener.SpigotListener;
import me.illusion.skyblockcore.bungee.utilities.StorageUtils;
import me.illusion.skyblockcore.bungee.utilities.YMLBase;
import me.illusion.skyblockcore.shared.dependency.DependencyDownloader;
import me.illusion.skyblockcore.shared.dependency.JedisUtil;
import me.illusion.skyblockcore.shared.packet.PacketManager;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;
import me.illusion.skyblockcore.shared.packet.impl.proxytoproxy.request.PacketRequestMessageSend;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.storage.StorageType;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkyblockBungeePlugin extends Plugin {

    private boolean enabled;

    private boolean multiProxy;

    private PlayerFinder playerFinder;
    private PacketManager packetManager;
    private Configuration config;
    private JedisUtil jedisUtil;
    private RedisListener redisListener;
    private StorageHandler storageHandler;
    private DependencyDownloader dependencyDownloader;

    @Override
    public void onEnable() {
        enabled = true;
        config = new YMLBase(this, "bungee-config.yml").getConfiguration();


        dependencyDownloader = new DependencyDownloader(getDataFolder().getParentFile());

        dependencyDownloader.onDownload(() -> {
            System.err.println("[SkyblockCore] Dependencies downloaded!");
            System.err.println("[SkyblockCore] Since dependencies have been downloaded, you will need to restart your server.");
            disable();
        });

        setupStorage().thenAccept(success -> {
            if (!success)
                return;

            setupJedis();

            getProxy().getPluginManager().registerCommand(this, new SkyblockCommand(this));
            getProxy().getPluginManager().registerListener(this, new ConnectListener());
            packetManager = new PacketManager();
            playerFinder = new PlayerFinder(this);

            setupPackets();
        });

    }

    private void setupPackets() {
        packetManager.registerProcessor(PacketDirection.PROXY_TO_PROXY, new RedisListener(this));
        packetManager.registerProcessor(PacketDirection.PROXY_TO_INSTANCE, new SpigotListener(this));

        packetManager.subscribe(PacketRequestMessageSend.class, new MessagePacketHandler());
    }

    @Override
    public void onDisable() {
        enabled = false;
    }

    /**
     * Opens the SQL connection async
     */
    private CompletableFuture<Boolean> setupStorage() {
        StorageType type = StorageType.valueOf(config.getString("database.type").toUpperCase(Locale.ROOT));

        if (type == StorageType.SQLITE) {
            System.err.println("Proxies are not supported for SQLite!");
            return CompletableFuture.completedFuture(false);
        }

        Class<? extends StorageHandler> clazz = type.getHandlerClass();
        try {
            storageHandler = clazz.newInstance();

            System.out.println("Created handler of type " + clazz.getSimpleName());
            return storageHandler.setup(getDataFolder(), StorageUtils.asMap(config.getSection("database")));

        } catch (InstantiationException | IllegalAccessException e) {
            ExceptionLogger.log(e);
        }

        return CompletableFuture.supplyAsync(() -> false);

    }

    private void setupJedis() {
        multiProxy = config.getBoolean("jedis.enable");

        if (!multiProxy)
            return;

        dependencyDownloader.dependOn(
                "redis.clients.Jedis",
                "https://www.illusionthe.dev/dependencies/Skyblock.html",
                "SkyblockDependencies.jar"
        );

        jedisUtil = new JedisUtil();

        String ip = config.getString("jedis.host");
        String port = config.getString("jedis.port", "");
        String password = config.getString("jedis.password", "");


        if (!jedisUtil.connect(ip, port, password))
            disable();
        else
            redisListener = new RedisListener(this);
    }

    private void disable() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getPluginManager().unregisterCommands(this);
        onDisable();
    }

}
