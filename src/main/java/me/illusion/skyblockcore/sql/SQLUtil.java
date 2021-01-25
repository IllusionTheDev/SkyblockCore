package me.illusion.skyblockcore.sql;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import static me.illusion.skyblockcore.sql.SQLOperation.*;

@Getter
public class SQLUtil {

    private static final String[] TABLES = {
            CREATE_DATA_TABLE,
            CREATE_ISLAND_TABLE,
            CREATE_UUID_TABLE,
            CREATE_ISLAND_DATA_TABLE
    };

    private Connection connection;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    public SQLUtil(String host, String database, String username, String password, int port) {

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
            connection.setAutoCommit(true);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void createTable() {
        CompletableFuture.runAsync(() -> {
            for (String query : TABLES) {
                try {
                    connection.createStatement().execute(query);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }


}