package me.illusion.skyblockcore.spigot;

import lombok.Getter;
import me.illusion.skyblockcore.shared.dependency.DependencyDownloader;
import me.illusion.skyblockcore.shared.packet.PacketManager;
import me.illusion.skyblockcore.shared.packet.data.PacketDirection;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.storage.StorageType;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.command.impl.CommandManager;
import me.illusion.skyblockcore.spigot.command.island.information.IslandHelpCommand;
import me.illusion.skyblockcore.spigot.command.island.invite.IslandInviteCommand;
import me.illusion.skyblockcore.spigot.command.island.movement.IslandGoCommand;
import me.illusion.skyblockcore.spigot.command.island.movement.IslandSetSpawnCommand;
import me.illusion.skyblockcore.spigot.data.PlayerManager;
import me.illusion.skyblockcore.spigot.file.ConfigurationStore;
import me.illusion.skyblockcore.spigot.file.IslandConfig;
import me.illusion.skyblockcore.spigot.file.SettingsFile;
import me.illusion.skyblockcore.spigot.file.SetupData;
import me.illusion.skyblockcore.spigot.island.IslandDependencies;
import me.illusion.skyblockcore.spigot.island.IslandManager;
import me.illusion.skyblockcore.spigot.island.world.EmptyWorldGenerator;
import me.illusion.skyblockcore.spigot.listener.DeathListener;
import me.illusion.skyblockcore.spigot.listener.DebugListener;
import me.illusion.skyblockcore.spigot.listener.JoinListener;
import me.illusion.skyblockcore.spigot.listener.LeaveListener;
import me.illusion.skyblockcore.spigot.messaging.CommunicationRegistry;
import me.illusion.skyblockcore.spigot.pasting.PastingHandler;
import me.illusion.skyblockcore.spigot.pasting.PastingType;
import me.illusion.skyblockcore.spigot.utilities.storage.MessagesFile;
import me.illusion.skyblockcore.spigot.utilities.storage.YMLBase;
import me.illusion.skyblockcore.spigot.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkyblockPlugin extends JavaPlugin {

    private StorageHandler storageHandler;
    private IslandManager islandManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;
    private WorldManager worldManager;
    private DependencyDownloader dependencyDownloader;
    private PacketManager packetManager;
    private SetupData setupData;

    private IslandDependencies islandDependencies;
    private ConfigurationStore files;

    @Override
    public void onEnable() {

        commandManager = new CommandManager(this);

        dependencyDownloader = new DependencyDownloader(getDataFolder().getParentFile());
        dependencyDownloader.onDownload(() -> {
            System.err.println("[SkyblockCore] Dependencies downloaded!");
            System.err.println("[SkyblockCore] Since you have downloaded dependencies, you will need to restart the server.");
        });

        registerDefaultCommands();

        System.out.println("Registering configuration files");
        files = new ConfigurationStore(
                new MessagesFile(this),
                new SettingsFile(this),
                new IslandConfig(this));

        PastingHandler pastingHandler = PastingType.enable(this, files.getIslandConfig().getPastingSelection());
        PastingType type = pastingHandler.getType();

        islandDependencies = new IslandDependencies(new EmptyWorldGenerator(this), pastingHandler, startFiles(type));


        setupData = new SetupData(this);


        System.out.println("Creating worlds");
        worldManager = new WorldManager(this);
        // Loads the SQL, when that's complete with a response (true|false), loads if false
        setupStorage().whenComplete((val, throwable) -> {
            if (!val) // if the setup is incorrect, don't load
                return;

            sync(this::load);
        });

        ExceptionLogger.setFolder(new File(getDataFolder() + File.separator + "log"));

    }

    private void load() {

        islandManager = new IslandManager(this);
        playerManager = new PlayerManager();

        System.out.println("Setting up island dependencies");


        System.out.println("Registering listeners");
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DebugListener(this), this);


        if (SpigotConfig.bungee) {
            System.out.println("Registering bungeecord messaging listener");
            packetManager = new PacketManager(files.getSettings().getConfiguration().getString("communication.server-id"));
            packetManager.registerProcessor(PacketDirection.INSTANCE_TO_PROXY, CommunicationRegistry.getChosenProcessor(this));

        }


        System.out.println("Loaded");

    }

    /**
     * Generates the empty island worlds
     */
    public void setupWorld(String name) {
        World world = new WorldCreator(name)
                .generator(islandDependencies.getEmptyWorldGenerator())
                .generateStructures(false)
                .seed(0)
                .createWorld();

        world.save();
        world.setAutoSave(false); // Disable auto-saving
        world.setKeepSpawnInMemory(false); // Disable spawn chunk loading
        world.setPVP(false); // Disable PVP

        CompletableFuture.runAsync(() -> {
            YMLBase yml = new YMLBase(this, new File(getDataFolder().getParentFile().getParentFile(), "bukkit.yml"), false);

            yml.getConfiguration().set("worlds." + name + ".generator", "Skyblock");
            yml.save();
        });
    }

    /**
     * Opens the SQL connection async
     */
    private CompletableFuture<Boolean> setupStorage() {
        FileConfiguration config = files.getSettings().getConfiguration();
        StorageType type = StorageType.valueOf(config.getString("database.type").toUpperCase(Locale.ROOT));
        type.checkDependencies(dependencyDownloader);

        Class<? extends StorageHandler> clazz = type.getHandlerClass();
        try {
            storageHandler = clazz.newInstance();


            System.out.println("Created handler of type " + clazz.getSimpleName());
            return storageHandler.setup(getDataFolder(), config.getConfigurationSection("database").getValues(false));

        } catch (InstantiationException | IllegalAccessException e) {
            ExceptionLogger.log(e);
        }

        return CompletableFuture.supplyAsync(() -> false);

    }

    private void registerDefaultCommands() {
        commandManager.register(new IslandGoCommand(this, "island"));
        commandManager.register(new IslandGoCommand(this, "island.go"));
        commandManager.register(new IslandGoCommand(this, "island.spawn"));
        commandManager.register(new IslandSetSpawnCommand(this));
        commandManager.register(new IslandInviteCommand(this));
        commandManager.register(new IslandHelpCommand(this));
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.get(player).save();
    }

    public File[] startFiles(PastingType type) {
        File startSchematicFolder = new File(getDataFolder() + File.separator + "start-schematic");

        if (!startSchematicFolder.exists())
            startSchematicFolder.mkdir();

        File[] files = startSchematicFolder.listFiles();

        if (files == null || files.length == 0) {
            if (type == PastingType.FAWE)
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
        return islandDependencies.getEmptyWorldGenerator();
    }
}
