package me.illusion.skyblockcore.sql;

import org.bukkit.Bukkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

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
     * @throws SQLException if the operation isn't successful
     */
    public static long serialize(Connection connection,
                                 Object objectToSerialize, String table) throws SQLException {

        PreparedStatement pstmt = connection
                .prepareStatement(SQL_SERIALIZE_OBJECT.replaceFirst("\\?", table), Statement.RETURN_GENERATED_KEYS);

        // just setting the class name
        pstmt.setString(1, objectToSerialize.getClass().getName());
        pstmt.setObject(2, objectToSerialize);
        pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
        int serialized_id = -1;
        if (rs.next()) {
            serialized_id = rs.getInt(1);
        }

        rs.close();
        pstmt.close();
        Bukkit.getLogger().info("Serialized object with id " + serialized_id);
        return serialized_id;
    }

    /**
     * Deserializes a SQL object
     *
     * @param connection    - The SQL connection
     * @param serialized_id - The serialized ID
     * @return deserialized object
     * @throws SQLException           if the operation isn't successful
     * @throws IOException            if the object stream is inaccessible
     * @throws ClassNotFoundException if the object class is not found
     */
    public static Object deserialize(Connection connection,
                                     long serialized_id, String table) throws SQLException, IOException,
            ClassNotFoundException {
        PreparedStatement pstmt = connection
                .prepareStatement(SQL_DESERIALIZE_OBJECT.replaceFirst("\\?", table));
        pstmt.setLong(1, serialized_id);
        ResultSet rs = pstmt.executeQuery();
        rs.next();

        // Object object = rs.getObject(1);

        Bukkit.getLogger().info("Deserializing object with id " + serialized_id);
        byte[] buf = rs.getBytes(1);
        ObjectInputStream objectIn = null;
        if (buf != null)
            objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

        Object deSerializedObject = objectIn.readObject();

        objectIn.close();
        rs.close();
        pstmt.close();

        return deSerializedObject;
    }
}
