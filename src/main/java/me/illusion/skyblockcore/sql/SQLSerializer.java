package me.illusion.skyblockcore.sql;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
     * @return the serialized ID
     */
    @SneakyThrows
    public static long serialize(Connection connection, Object objectToSerialize, String table) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int serialized_id = -1;

        try {
            pstmt = connection
                    .prepareStatement(SQL_SERIALIZE_OBJECT.replaceFirst("\\?", table), Statement.RETURN_GENERATED_KEYS);

            // just setting the class name
            pstmt.setString(1, objectToSerialize.getClass().getName());
            pstmt.setObject(2, objectToSerialize);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next())
                serialized_id = rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }


        Bukkit.getLogger().info("Serialized object with id " + serialized_id);
        return serialized_id;
    }

    /**
     * Deserializes a SQL object
     *
     * @param connection    - The SQL connection
     * @param serialized_id - The serialized ID
     * @return deserialized object
     */
    @SneakyThrows
    public static Object deserialize(Connection connection, long serialized_id, String table) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ObjectInputStream objectIn = null;

        Object deSerializedObject = null;

        try {
            pstmt = connection.prepareStatement(SQL_DESERIALIZE_OBJECT.replaceFirst("\\?", table));
            pstmt.setLong(1, serialized_id);

            rs = pstmt.executeQuery();
            rs.next();

            // Object object = rs.getObject(1);

            Bukkit.getLogger().info("Deserializing object with id " + serialized_id);
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
