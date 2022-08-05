package me.illusion.skyblockcore.shared.sql;

import me.illusion.skyblockcore.shared.sql.serializing.DynamicTable;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SQLConnectionProvider {

    protected Connection connection;
    private final Map<String, DynamicTable> dynamicTables = new ConcurrentHashMap<>();

    public Connection get() {
        try {
            if (connection == null || !connection.isValid(150))
                load();
        } catch (SQLException e) {
            load();
        }

        return connection;
    }

    public CompletableFuture<DynamicTable> getDynamicTable(String tableName) {
        DynamicTable table = dynamicTables.get(tableName);

        if (table == null)
            return createDynamicTable(tableName);

        return CompletableFuture.completedFuture(table);
    }

    public CompletableFuture<DynamicTable> createDynamicTable(String tableName) {
        return CompletableFuture.supplyAsync(() -> {
            DynamicTable table = new DynamicTable(tableName);

            String createQuery = table.getCreationQuery();

            try {
                get().prepareStatement(createQuery).execute();
            } catch (SQLException e) {
                ExceptionLogger.log(e);
            }
            dynamicTables.put(tableName, table);
            return table;
        });
    }

    public abstract void load();
}