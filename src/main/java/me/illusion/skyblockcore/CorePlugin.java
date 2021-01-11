package me.illusion.skyblockcore;

import lombok.Getter;
import me.illusion.skyblockcore.data.PlayerManager;
import me.illusion.skyblockcore.file.IslandConfig;
import me.illusion.skyblockcore.island.IslandManager;
import me.illusion.skyblockcore.island.grid.IslandGrid;
import me.illusion.skyblockcore.island.world.EmptyWorldGenerator;
import me.illusion.skyblockcore.listener.JoinListener;
import me.illusion.skyblockcore.listener.LeaveListener;
import me.illusion.skyblockcore.sql.SQLUtil;
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
    private IslandGrid grid;

    private MessagesFile messages;
    private File startSchematic;

    @Override
    public void onEnable() {
        startSchematic = new File(getDataFolder(), "skyblock-schematic.schematic");

        if (!startSchematic.exists())
            saveResource("skyblock-schematic.schematic", false);

        setupSQL();

        messages      = new MessagesFile(this);
        islandConfig  = new IslandConfig(this);
        grid          = new IslandGrid(5, 5);
        islandManager = new IslandManager(this);
        playerManager = new PlayerManager();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::setupWorld, 1L);

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(this), this);
    }

    /**
     * Generates the empty island worlds
     */
    private void setupWorld() {
        new WorldCreator(islandConfig.getOverworldSettings().getWorld())
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
            SQLUtil sql = new SQLUtil(this, host, database, username, password, port);

            if (!sql.openConnection()) {
                getLogger().warning("Could not load SQL Database.");
                getLogger().warning("This plugin requires a valid SQL database to work.");
                getPluginLoader().disablePlugin(this);
                return;
            }

            sql.createTable();
            mySQLConnection = sql.getConnection();
        });
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.get(player).save();
    }
}
