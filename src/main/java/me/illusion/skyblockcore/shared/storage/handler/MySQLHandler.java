package me.illusion.skyblockcore.shared.storage.handler;

import me.illusion.skyblockcore.shared.sql.SQLConnectionProvider;
import me.illusion.skyblockcore.shared.sql.SQLSerializer;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.shared.sql.SQLOperation.CREATE_DATA_TABLE;
import static me.illusion.skyblockcore.shared.sql.SQLOperation.CREATE_ISLAND_DATA_TABLE;

public class MySQLHandler extends SQLConnectionProvider implements StorageHandler {

    private static final String[] TABLES = {
            CREATE_DATA_TABLE,
            CREATE_ISLAND_DATA_TABLE
    };
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    @Override
    public CompletableFuture<Boolean> setup(File folder, Map<String, Object> map) {
        return CompletableFuture.supplyAsync(() -> {

            host = map.get("host").toString();
            port = Integer.parseInt(map.get("port").toString());
            database = map.get("database").toString();
            username = map.get("username").toString();
            password = map.get("password").toString();

            if (!loadConnection()) {
                System.out.println("Failed to connect to MySQL server.");
                return false;
            }

            return true;
        });

    }


    @Override
    public CompletableFuture<Object> get(UUID uuid, String category) {
        System.out.println("Getting data from MySQL server.");
        return SQLSerializer.deserialize(get(), uuid, category);
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, Object object, String category) {
        return CompletableFuture.runAsync(() -> SQLSerializer.serialize(get(), uuid, object, category)).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });
    }

    @Override
    public void load() {
        loadConnection();
    }

    private boolean loadConnection() {
        try {
            connection = null;

            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoreconnect=true", username, password);

            for (String query : TABLES) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
            }
            return true;
        } catch (Exception ignored) {
            // We know it's not loaded, so we can safely ignore it, as the plugin will not run any further

            // e.printStackTrace();
            return false;
        }
    }
}
