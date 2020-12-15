package me.illusion.skyblockcore.sql;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class SQLUtil {

    private static final String[] TABLES = {
            "CREATE TABLE IF NOT EXISTS uuid_data (uuid VARCHAR(36), id LONG);",
            "CREATE TABLE IF NOT EXISTS island_data (uuid VARCHAR(36), id LONG);",
            "CREATE TABLE IF NOT EXISTS serialized_java_objects (serialized_id int(11) NOT NULL auto_increment, object_name varchar(20) default NULL, serialized_object blob, PRIMARY KEY (serialized_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;"
    };

    private final JavaPlugin main; //MAIN

    private Connection connection;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    public SQLUtil(JavaPlugin main, String host, String database, String username, String password, int port) {
        this.main = main;

        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public boolean openConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void createTable() {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                for(String query : TABLES)
                    connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


}