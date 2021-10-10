package me.illusion.skyblockcore.shared.storage.handler;

import me.illusion.skyblockcore.shared.sql.SQLUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SQLiteHandler extends MySQLHandler {

    @Override
    public CompletableFuture<Boolean> setup(File folder) {
        File file = new File(folder + File.separator + "storage", "database.db");

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return CompletableFuture.supplyAsync(() -> {
            SQLUtil sql = new SQLUtil(file);

            if (!sql.openConnection()) {
                return false;
            }

            sql.createTable();
            connection = sql.getConnection();
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> setup(String ip, int port, String database, String username, String password) {
        return CompletableFuture.supplyAsync(() -> false);
    }

    @Override
    public boolean isFileBased() {
        return true;
    }
}
