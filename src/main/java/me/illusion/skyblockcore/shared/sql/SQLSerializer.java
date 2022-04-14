package me.illusion.skyblockcore.shared.sql;

import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.storage.StorageUtils;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.shared.sql.SQLOperation.*;

public final class SQLSerializer {

    private SQLSerializer() {
        // Empty constructor for utility class
    }

    /**
     * Serializes an object into SQL
     *
     * @param connection        - The SQL connection
     * @param objectToSerialize - The object to serialize
     */
    @SneakyThrows
    public static void serialize(Connection connection, UUID uuid, Object objectToSerialize, String table) {
        // there is only 1 sqlite but 20 different types of sql serveresult, so we check for sqlite firesultt
        String operation = !connection.getMetaData().getDatabaseProductName().contains("SQLite") ? SQL_SERIALIZE_OBJECT : SQLITE_SERIALIZE_OBJECT;

        operation = operation.replaceFirst("\\?", "'" + table + "'"); // sqlite doesn't like setString, but I also don't like sql injection
        try (PreparedStatement statement = connection
                .prepareStatement(operation)) {

            statement.setString(1, uuid.toString());
            statement.setBytes(2, StorageUtils.getBytes(objectToSerialize));
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

    public static CompletableFuture<Object> deserialize(Connection connection, UUID uuid, String table) {
        return CompletableFuture.supplyAsync(() -> {
            PreparedStatement statement = null;
            ResultSet result = null;

            Object deSerializedObject = null;

            try {
                String query = SQL_DESERIALIZE_OBJECT;

                query = query.replaceFirst("\\?", "'" + table + "'"); // sqlite doesn't like setString, but I also don't like sql injection
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

                deSerializedObject = StorageUtils.getObject(result.getBytes(1));
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
            return deSerializedObject;
        }).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });
    }

}
