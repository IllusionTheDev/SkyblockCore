package me.illusion.skyblockcore.sql;

import lombok.SneakyThrows;
import me.illusion.utilities.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static me.illusion.skyblockcore.sql.SQLOperation.SQL_DESERIALIZE_OBJECT;
import static me.illusion.skyblockcore.sql.SQLOperation.SQL_SERIALIZE_OBJECT;

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
    @SneakyThrows
    public static Object deserialize(Connection connection, UUID uuid, String table) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ObjectInputStream objectIn = null;

        Object deSerializedObject = null;

        try {
            pstmt = connection.prepareStatement(StringUtil.replaceFirst(SQL_DESERIALIZE_OBJECT, '?', table));
            pstmt.setString(1, uuid.toString());

            rs = pstmt.executeQuery();
            rs.next();

            // Object object = rs.getObject(1);

            byte[] buf = rs.getBytes(1);
            if (buf != null)
                objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

            deSerializedObject = objectIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null)
                objectIn.close();
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }

        return deSerializedObject;
    }
}
