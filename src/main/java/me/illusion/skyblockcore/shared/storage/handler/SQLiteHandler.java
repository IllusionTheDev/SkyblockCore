package me.illusion.skyblockcore.shared.storage.handler;

import me.illusion.skyblockcore.shared.sql.SQLUtil;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SQLiteHandler extends MySQLHandler {

    @Override
    public CompletableFuture<Boolean> setup(File folder, Map<String, Object> config) {
        File file = new File(folder + File.separator + "storage", "database.db");

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            ExceptionLogger.log(e);
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

}
