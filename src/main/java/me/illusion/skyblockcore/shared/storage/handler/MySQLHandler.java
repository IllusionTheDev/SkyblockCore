package me.illusion.skyblockcore.shared.storage.handler;

import me.illusion.skyblockcore.shared.sql.SQLSerializer;
import me.illusion.skyblockcore.shared.sql.SQLUtil;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLHandler implements StorageHandler {

    protected Connection connection;

    @Override
    public CompletableFuture<Boolean> setup(File folder, Map<String, Object> map) {
        return CompletableFuture.supplyAsync(() -> {

            String ip = map.get("host").toString();
            int port = Integer.parseInt(map.get("port").toString());
            String database = map.get("database").toString();
            String username = map.get("username").toString();
            String password = map.get("password").toString();

            SQLUtil sql = new SQLUtil(ip, database, username, password, port);

            if (!sql.openConnection()) {
                return false;
            }

            sql.createTable();
            connection = sql.getConnection();
            return true;
        });

    }


    @Override
    public CompletableFuture<Object> get(UUID uuid, String category) {
        return SQLSerializer.deserialize(connection, uuid, category);
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, Object object, String category) {
        return CompletableFuture.runAsync(() -> SQLSerializer.serialize(connection, uuid, object, category)).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });
    }

}
