package me.illusion.skyblockcore.shared.sql;

import me.illusion.skyblockcore.shared.utilities.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.shared.sql.SQLOperation.SQL_DESERIALIZE_OBJECT;
import static me.illusion.skyblockcore.shared.sql.SQLOperation.SQL_SERIALIZE_OBJECT;

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
    public static void serialize(Connection connection, UUID uuid, Object objectToSerialize, String table) {
        try (PreparedStatement pstmt = connection
                .prepareStatement(StringUtil.replaceFirst(SQL_SERIALIZE_OBJECT, '?', table))) {

            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, objectToSerialize.getClass().getName());
            pstmt.setObject(3, objectToSerialize);
            pstmt.executeUpdate();

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
        return CompletableFuture.supplyAsync(() -> {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            ObjectInputStream objectIn = null;

            Object deSerializedObject = null;

            try {
                pstmt = connection.prepareStatement(StringUtil.replaceFirst(SQL_DESERIALIZE_OBJECT, '?', table));
                pstmt.setString(1, uuid.toString());

                rs = pstmt.executeQuery();

                if (!rs.next())
                    return null;

                // Object object = rs.getObject(1);

                byte[] buf = rs.getBytes(1);
                if (buf != null)
                    objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

                deSerializedObject = objectIn.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (objectIn != null)
                        objectIn.close();
                    //if (rs != null && !rs.isClosed())
                    //    rs.close();
                    //if (pstmt != null && !pstmt.isClosed())
                    //    pstmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return deSerializedObject;
        });
    }
}
