package me.illusion.skyblockcore;

import lombok.Getter;
import me.illusion.skyblockcore.command.CommandManager;
import me.illusion.skyblockcore.command.island.IslandCommand;
import me.illusion.skyblockcore.data.PlayerManager;
import me.illusion.skyblockcore.file.IslandConfig;
import me.illusion.skyblockcore.hook.VaultHook;
import me.illusion.skyblockcore.island.IslandManager;
import me.illusion.skyblockcore.island.world.EmptyWorldGenerator;
import me.illusion.skyblockcore.listener.JoinListener;
import me.illusion.skyblockcore.listener.LeaveListener;
import me.illusion.skyblockcore.pasting.PastingHandler;
import me.illusion.skyblockcore.pasting.PastingType;
import me.illusion.skyblockcore.sql.SQLUtil;
import me.illusion.skyblockcore.world.WorldManager;
import me.illusion.utilities.storage.MessagesFile;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

@Getter
public class CorePlugin extends JavaPlugin {

    private Connection mySQLConnection;
    private IslandConfig islandConfig;
    private IslandManager islandManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;
    private WorldManager worldManager;
    private PastingHandler pastingHandler;

    private MessagesFile messages;
    private File startSchematic;

    @Override
    public void onEnable() {
        startSchematic = new File(getDataFolder(), "skyblock-schematic.schematic");

        if (!startSchematic.exists())
            saveResource("skyblock-schematic.schematic", false);

        setupSQL();

        messages = new MessagesFile(this);
        islandConfig = new IslandConfig(this);
        islandManager = new IslandManager(this);
        commandManager = new CommandManager(this);
        playerManager = new PlayerManager();

        worldManager = new WorldManager(this);
        pastingHandler = PastingType.enable(this, islandConfig.getPastingSelection());

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(this), this);

        registerDefaultCommands();

        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
            new VaultHook(this);
    }

    /**
     * Generates the empty island worlds
     */
    public void setupWorld(String name) {
        new WorldCreator(name)
                .generator(new EmptyWorldGenerator())
                .generateStructures(false)
                .createWorld();
    }

    /**
     * Opens the SQL connection async
     */
    private void setupSQL() {
        saveDefaultConfig();

        String host = getConfig().getString("database.host");
        String database = getConfig().getString("database.database");
        String username = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");
        int port = getConfig().getInt("database.port");

        CompletableFuture.runAsync(() -> {
            SQLUtil sql = new SQLUtil(host, database, username, password, port);

            if (!sql.openConnection()) {
                getLogger().warning("Could not load SQL Database.");
                getLogger().warning("This plugin requires a valid SQL database to work.");
                setEnabled(false);
                return;
            }

            sql.createTable();
            mySQLConnection = sql.getConnection();
        });
    }

    private void registerDefaultCommands() {
        commandManager.register(new IslandCommand(this));
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.get(player).save();
    }
}
