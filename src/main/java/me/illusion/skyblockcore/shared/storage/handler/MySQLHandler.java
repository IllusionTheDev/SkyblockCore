package me.illusion.skyblockcore.shared.storage.handler;

import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.sql.SQLConnectionProvider;
import me.illusion.skyblockcore.shared.sql.SQLSerializer;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.storage.StorageUtils;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.shared.sql.SQLOperation.*;

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
    public CompletableFuture<SkyblockSerializable> get(UUID uuid, String category) {
        System.out.println("Getting data from MySQL server.");
        return SQLSerializer.deserialize(get(), uuid, category);
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, SkyblockSerializable object, String category) {
        return CompletableFuture.runAsync(() -> SQLSerializer.serialize(this, uuid, StorageUtils.process(object), category)).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> delete(UUID uuid, String category) {
        return CompletableFuture.runAsync(() -> {
            Connection con = get();

            String operation = SQL_DELETE_OBJECT.replaceFirst("\\?", category);

            try (PreparedStatement statement = con.prepareStatement(operation)) {
                statement.setString(1, uuid.toString());

                statement.execute();
            } catch (SQLException exception) {
                ExceptionLogger.log(exception);
            }
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
