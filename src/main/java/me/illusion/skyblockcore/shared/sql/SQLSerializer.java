package me.illusion.skyblockcore.shared.sql;

import lombok.SneakyThrows;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.shared.utilities.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        try (PreparedStatement statement = connection
                .prepareStatement(StringUtil.replaceFirst(operation, '?', table))) {

            System.out.println(objectToSerialize.getClass().getName() + " is being serialized for " + uuid);
            statement.setString(1, uuid.toString());
            statement.setBytes(2, getBytes(objectToSerialize));
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes a SQL object
     *
     * @param connection - The SQL connection
     * @return deserialized object
     */

    public static CompletableFuture<Object> deserialize(Connection connection, UUID uuid, String table) {
        System.out.println("Deserializing " + uuid + " from " + table);
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Started Future");
            PreparedStatement statement = null;
            ResultSet result = null;

            Object deSerializedObject = null;

            try {
                System.out.println("Started try");
                statement = connection.prepareStatement(StringUtil.replaceFirst(SQL_DESERIALIZE_OBJECT, '?', table));
                statement.setString(1, uuid.toString());

                System.out.println("Prepared statement");

                result = statement.executeQuery();

                System.out.println("Executed query");
                if (!result.next())
                    return null;

                System.out.println("Got next");
                deSerializedObject = getObject(result.getBytes(1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (statement != null)
                    statement.close();
                if (result != null)
                    result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Finished");
            return deSerializedObject;
        }).exceptionally(throwable -> {
            ExceptionLogger.log(throwable);
            return null;
        });
    }

    private static byte[] getBytes(Object object) {
        try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
             ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput)) {

            objectOutput.writeObject(object);
            return byteOutput.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object getObject(byte[] bytes) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
             ObjectInputStream objectIn = new ObjectInputStream(byteIn)) {

            return objectIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
