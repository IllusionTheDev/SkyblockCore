package me.illusion.skyblockcore.sql;

import org.bukkit.Bukkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

public class SQLSerializer {

    private static final String SQL_SERIALIZE_OBJECT = "INSERT INTO serialized_java_objects(object_name, serialized_object) VALUES (?, ?)";
    private static final String SQL_DESERIALIZE_OBJECT = "SELECT serialized_object FROM serialized_java_objects WHERE serialized_id = ?";

    public static long serialize(Connection connection,
                                 Object objectToSerialize) throws SQLException {

        PreparedStatement pstmt = connection
                .prepareStatement(SQL_SERIALIZE_OBJECT, Statement.RETURN_GENERATED_KEYS);

        // just setting the class name
        System.out.println(objectToSerialize.getClass().getName().length());
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
     * To de-serialize a java object from database
     *
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(Connection connection,
                                     long serialized_id) throws SQLException, IOException,
            ClassNotFoundException {
        PreparedStatement pstmt = connection
                .prepareStatement(SQL_DESERIALIZE_OBJECT);
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

        rs.close();
        pstmt.close();

        return deSerializedObject;
    }
}
