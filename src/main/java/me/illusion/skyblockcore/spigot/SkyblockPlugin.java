package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.skyblockcore.shared.dependency.DependencyDownloader;
import me.illusion.skyblockcore.shared.environment.Core;
import me.illusion.skyblockcore.shared.environment.EnvironmentUtil;
import me.illusion.skyblockcore.shared.packet.PacketManager;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.storage.StorageType;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.command.impl.CommandManager;
import me.illusion.skyblockcore.spigot.command.island.information.IslandHelpCommand;
import me.illusion.skyblockcore.spigot.command.island.invite.IslandInviteCommand;
import me.illusion.skyblockcore.spigot.command.island.movement.IslandGoCommand;
import me.illusion.skyblockcore.spigot.data.PlayerManager;
import me.illusion.skyblockcore.spigot.file.IslandConfig;
import me.illusion.skyblockcore.spigot.file.SettingsFile;
import me.illusion.skyblockcore.spigot.hook.VaultHook;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.island.world.EmptyWorldGenerator;
import me.illusion.skyblockcore.spigot.listener.DeathListener;
import me.illusion.skyblockcore.spigot.listener.DebugListener;
import me.illusion.skyblockcore.spigot.listener.JoinListener;
import me.illusion.skyblockcore.spigot.listener.LeaveListener;
import me.illusion.skyblockcore.spigot.messaging.BungeeMessaging;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import me.illusion.skyblockcore.spigot.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.spigot.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkyblockPlugin extends JavaPlugin {

    /*
        The handler used for player and island data storage
     */
    private StorageHandler storageHandler;

    /*
        The island configuration
     */
    private IslandConfig islandConfig;

    /*
        The island manager, used to obtain islands
     */
    private IslandManager islandManager;

    /*
        The player manager, used to obtain SkyblockPlayers
     */
    private PlayerManager playerManager;

    /*
        The command manager, used to register and handle commands
     */
    private CommandManager commandManager;

    /*
        The world manager, used to assign worlds to islands
     */
    private WorldManager worldManager;

    /*
        The pasting handler, used to save / load islands from files
     */
    private PastingHandler pastingHandler;

    /*
        The empty world generator, seems obvious
     */
    private EmptyWorldGenerator emptyWorldGenerator;

    /*
        Bungee messaging handler, responsible for communication to proxy(ies)
     */
    private BungeeMessaging bungeeMessaging;

    /*
        Message file, used to send and obtain messages
     */
    private MessagesFile messages;

    /*
        Settings file, contains essential info that are crucial for the plugin,
        such as world anti-corruption delays and database information
     */
    private SettingsFile settings;

    /*
        Start schematics, default island on selected format
     */
    private File[] startSchematic;

    /*
        Dependency downloader, used to automatically download required drivers
     */
    private DependencyDownloader dependencyDownloader;

    private PacketManager packetManager;

    private static SkyblockPlugin instance;

    public static boolean enabled = false;

    @Override
    public void onEnable() {
        enabled = true;
        emptyWorldGenerator = new EmptyWorldGenerator(this);
        commandManager = new CommandManager(this);
        EnvironmentUtil.setLogger(getLogger());

        dependencyDownloader = new DependencyDownloader(getDataFolder());
        dependencyDownloader.onDownload(() -> {
            Core.warn("Dependencies downloaded!");
            Core.warn("Since you have downloaded dependencies, you will need to restart the server.");
        });

        registerDefaultCommands();

        Core.info("Registering configuration files");
        instance = this;
        messages = new MessagesFile(this);
        islandConfig = new IslandConfig(this);
        settings = new SettingsFile(this);

        Core.info("Creating worlds");
        worldManager = new WorldManager(this);
        // Loads the SQL, when that's complete with a response (true|false), loads if false
        setupStorage().whenComplete((val, throwable) -> {
            if (!val) // if the setup is incorrect, don't load
                return;

            try {
                sync(this::load);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ExceptionLogger.setFolder(new File(getDataFolder() + File.separator + "log"));

    }

    private void load() {

        islandManager = new IslandManager(this);
        playerManager = new PlayerManager();

        Core.info("Setting up pasting handler");
        pastingHandler = PastingType.enable(this, islandConfig.getPastingSelection());

        Core.info("Registering start files");
        startSchematic = startFiles();

        Core.info("Registering listeners");
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DebugListener(this), this);

        Core.info("Registering possible hooks");
        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
            new VaultHook(this);

        Core.info("Registering BungeeCord messaging listener");
        packetManager = new PacketManager();
        bungeeMessaging = new BungeeMessaging(this);

        Core.info("Loaded");

    }

    /**
     * Generates the empty island worlds
     *
     * @param name World name
     */
    public void setupWorld(String name) {
        World world = new WorldCreator(name)
                .generator("Skyblock")
                .generateStructures(false)
                .seed(0)
                .createWorld();

        world.setAutoSave(false); // Disable auto-saving
        world.setKeepSpawnInMemory(false); // Disable spawn chunk loading
        world.setPVP(false); // Disable PVP
    }

    /**
     * Opens the SQL connection async
     */
    private CompletableFuture<Boolean> setupStorage() {
        FileConfiguration config = settings.getConfiguration();
        StorageType type = StorageType.valueOf(config.getString("database.type").toUpperCase(Locale.ROOT));
        type.checkDependencies(this);

        Class<? extends StorageHandler> clazz = type.getHandlerClass();
        try {
            storageHandler = clazz.newInstance();

            if (storageHandler.isFileBased())
                return storageHandler.setup(getDataFolder());

            String host = config.getString("database.host", "");
            String database = config.getString("database.database", "");
            String username = config.getString("database.username", "");
            String password = config.getString("database.password", "");
            int port = config.getInt("database.port");

            if (host.equals("")) {
                Core.severe("Database host is unset! Please check configuration.");
            }

            getLogger().info("Created handler of type " + clazz.getSimpleName());
            return storageHandler.setup(host, port, database, username, password);

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return CompletableFuture.supplyAsync(() -> false);

    }

    private void registerDefaultCommands() {
        commandManager.register(new IslandGoCommand(this, "island"));
        commandManager.register(new IslandGoCommand(this, "island.go"));
        commandManager.register(new IslandInviteCommand(this));
        commandManager.register(new IslandHelpCommand(this));
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.get(player).save();
        enabled = false;
    }

    private File[] startFiles() {
        File startSchematicFolder = new File(getDataFolder() + File.separator + "start-schematic");

        if (!startSchematicFolder.exists())
            startSchematicFolder.mkdir();

        File[] files = startSchematicFolder.listFiles();

        if (files == null || files.length == 0) {
            if (pastingHandler.getType() == PastingType.FAWE)
                saveResource("start-schematic" + File.separator + "skyblock-schematic.schematic", false);
            else
                saveResource("start-schematic" + File.separator + "r.0.0.mca", false);
        }

        return startSchematicFolder.listFiles();
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return emptyWorldGenerator;
    }

    /**
     * Log
     * @param message message
     */
    @Deprecated
    public static void log(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        instance.getLogger().info(builder.toString());
    }

    /**
     * Output a warning
     * @param message content
     */
    @Deprecated
    public static void warn(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        instance.getLogger().warning(builder.toString());
    }

    @Deprecated
    public static void severe(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        instance.getLogger().severe(builder.toString());
    }
}
