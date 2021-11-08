package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.PacketManager;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.storage.StorageType;
import me.illusion.skyblockcore.spigot.command.CommandManager;
import me.illusion.skyblockcore.spigot.command.impl.OldCommandManager;
import me.illusion.skyblockcore.spigot.command.island.IslandCommand;
import me.illusion.skyblockcore.spigot.command.island.movement.IslandGoCommand;
import me.illusion.skyblockcore.spigot.data.PlayerManager;
import me.illusion.skyblockcore.spigot.file.IslandConfig;
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
import org.bukkit.WorldCreator;
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
        Start schematics, default island on selected format
     */
    private File[] startSchematic;

    private PacketManager packetManager;

    @Override
    public void onEnable() {
        emptyWorldGenerator = new EmptyWorldGenerator(this);

        System.out.println("Registering configuration files");
        messages = new MessagesFile(this);
        islandConfig = new IslandConfig(this);

        System.out.println("Creating worlds");
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

    }

    private void load() {

        islandManager = new IslandManager(this);
        commandManager = new OldCommandManager(this);
        playerManager = new PlayerManager();

        System.out.println("Setting up pasting handler");
        pastingHandler = PastingType.enable(this, islandConfig.getPastingSelection());

        System.out.println("Registering start files");
        startSchematic = startFiles();

        System.out.println("Registering listeners");
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DebugListener(this), this);

        System.out.println("Registering default commands.");
        registerDefaultCommands();

        System.out.println("Registering possible hooks");
        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
            new VaultHook(this);

        System.out.println("Registering bungeecord messaging listener");
        packetManager = new PacketManager();
        bungeeMessaging = new BungeeMessaging(this);

        System.out.println("Loaded");

    }

    /**
     * Generates the empty island worlds
     */
    public void setupWorld(String name) {
        new WorldCreator(name)
                .generator("Skyblock")
                .generateStructures(false)
                .seed(0)
                .createWorld();
    }

    /**
     * Opens the SQL connection async
     */
    private CompletableFuture<Boolean> setupStorage() {
        saveDefaultConfig();

        StorageType type = StorageType.valueOf(getConfig().getString("database.type").toUpperCase(Locale.ROOT));

        Class<? extends StorageHandler> clazz = type.getHandlerClass();
        try {
            storageHandler = clazz.newInstance();

            if (storageHandler.isFileBased())
                return storageHandler.setup(getDataFolder());

            String host = getConfig().getString("database.host", "");
            String database = getConfig().getString("database.database", "");
            String username = getConfig().getString("database.username", "");
            String password = getConfig().getString("database.password", "");
            int port = getConfig().getInt("database.port");

            return storageHandler.setup(host, port, database, username, password);

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return CompletableFuture.supplyAsync(() -> false);

    }

    private void registerDefaultCommands() {
        commandManager.register(new IslandCommand(this));
        commandManager.register(new IslandGoCommand(this));

        commandManager.runTests();
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.get(player).save();
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
}
