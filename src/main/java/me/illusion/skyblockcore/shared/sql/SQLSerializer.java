package me.illusion.skyblockcore.shared.sql;

import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.sql.serializing.DynamicTable;
import me.illusion.skyblockcore.shared.storage.StorageUtils;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.shared.sql.SQLOperation.SQL_DESERIALIZE_OBJECT;

public final class SQLSerializer {

    private SQLSerializer() {
        // Empty constructor for utility class
    }

    /**
     * Serializes an object into SQL
     */
    @SneakyThrows
    public static void serialize(SQLConnectionProvider provider, UUID uuid, Map<String, Object> map, String table) {
        Connection connection = provider.get();

        // there is only 1 sqlite but 20 different types of sql serveresult, so we check for sqlite firesultt
        System.out.println("Serializing " + map.get("classType") + " to SQL.");

        DynamicTable dynamicTable = provider.getDynamicTable(table).get();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            dynamicTable.adapt(connection, entry.getKey(), entry.getValue());
        }

        String operation = dynamicTable.getWriteQuery(map);

        operation = operation.replaceFirst("\\?", table); // sqlite doesn't like setString, but I also don't like sql injection
        try (PreparedStatement statement = connection
                .prepareStatement(operation)) {

            statement.setString(1, uuid.toString());

            statement.executeUpdate();

        } catch (Exception e) {
            ExceptionLogger.log(e);
        }
    }

    /**
     * Deserializes a SQL object
     *
     * @param connection - The SQL connection
     * @return deserialized object
     */

    public static CompletableFuture<SkyblockSerializable> deserialize(Connection connection, UUID uuid, String table) {
        return CompletableFuture.supplyAsync(() -> {
            PreparedStatement statement = null;
            ResultSet result = null;

            SkyblockSerializable object = null;

            try {
                String query = SQL_DESERIALIZE_OBJECT;

                query = query.replaceFirst("\\?", table); // sqlite doesn't like setString, but I also don't like sql injection
                query = query.replaceFirst("\\?", "'" + uuid.toString() + "'");

                System.out.println(query);
                statement = connection.prepareStatement(query);
                /*
                statement.setString(1, table);
                statement.setString(2, uuid.toString());


                 */
                result = statement.executeQuery();

                if (!result.next())
                    return null;

                Map<String, Object> map = new HashMap<>();

                // get all the columns
                // and fit into map
                for (int column = 1; column <= result.getMetaData().getColumnCount(); column++) {
                    map.put(result.getMetaData().getColumnLabel(column), result.getObject(column));
                }

                object = StorageUtils.unserialize(map);
            } catch (Exception e) {
                ExceptionLogger.log(e);
            }

            try {
                if (statement != null)
                    statement.close();
                if (result != null)
                    result.close();
            } catch (SQLException e) {
                ExceptionLogger.log(e);
            }
            return object;
        }).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });
    }

}
