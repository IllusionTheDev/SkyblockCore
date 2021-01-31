package me.illusion.skyblockcore.bungee;

import lombok.Getter;
import me.illusion.skyblockcore.bungee.command.SkyblockCommand;
import me.illusion.skyblockcore.bungee.utilities.YMLBase;
import me.illusion.skyblockcore.bungee.utilities.database.JedisUtil;
import me.illusion.skyblockcore.shared.sql.SQLUtil;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkyblockBungeePlugin extends Plugin {

    private boolean enabled;

    private boolean multiProxy;

    private PlayerFinder playerFinder;
    private Connection mySQLConnection;
    private Configuration config;
    private JedisUtil jedisUtil;

    @Override
    public void onEnable() {
        enabled = true;
        config = new YMLBase(this, "bungee-config.yml").getConfiguration();
        setupSQL();

        if (!enabled)
            return;

        setupJedis();

        if (!enabled)
            return;

        getProxy().getPluginManager().registerCommand(this, new SkyblockCommand(this));
        playerFinder = new PlayerFinder(this);
    }

    @Override
    public void onDisable() {
        enabled = false;
    }

    /**
     * Opens the SQL connection async
     */
    private void setupSQL() {
        String host = config.getString("database.host", "");
        String database = config.getString("database.database", "");
        String username = config.getString("database.username", "");
        String password = config.getString("database.password", "");
        int port = config.getInt("database.port");

        CompletableFuture.runAsync(() -> {
            SQLUtil sql = new SQLUtil(host, database, username, password, port);

            if (!sql.openConnection()) {
                getLogger().warning("Could not load SQL Database.");
                getLogger().warning("This plugin requires a valid SQL database to work.");
                disable();
                return;
            }

            sql.createTable();
            mySQLConnection = sql.getConnection();
        });
    }

    private void setupJedis() {
        multiProxy = config.getBoolean("jedis.enable");

        if (!multiProxy)
            return;

        jedisUtil = new JedisUtil();

        String ip = config.getString("jedis.host");
        String port = config.getString("jedis.port", "");
        String password = config.getString("jedis.password", "");

        if (!jedisUtil.connect(this, ip, port, password))
            disable();
    }

    private void disable() {
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getPluginManager().unregisterCommands(this);
        onDisable();
    }

}
