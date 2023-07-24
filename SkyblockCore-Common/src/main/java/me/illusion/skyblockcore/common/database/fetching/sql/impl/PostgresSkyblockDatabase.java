package me.illusion.skyblockcore.common.database.fetching.sql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.fetching.sql.AbstractSQLSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.SkyblockSQLQuery;

public class PostgresSkyblockDatabase extends AbstractSQLSkyblockDatabase {

    private static final String FETCH_ISLAND_ID = "SELECT island_id FROM island_id WHERE player_id = ?";
    private static final String FETCH_ISLAND_DATA = "SELECT * FROM island_data WHERE island_id = ?";
    private static final String DELETE_ISLAND_DATA = "DELETE FROM island_data WHERE island_id = ?";
    private static final String DELETE_ISLAND_ID = "DELETE FROM island_id WHERE island_id = ?";
    private static final String SAVE_ISLAND_DATA = "INSERT INTO island_data (island_id, player_id) VALUES (?, ?) ON CONFLICT (island_id) DO UPDATE SET player_id = ?";
    private static final String SAVE_ISLAND_ID = "INSERT INTO island_id (player_id, island_id) VALUES (?, ?) ON CONFLICT (player_id) DO UPDATE SET island_id = ?";
    private static final String CREATE_ISLAND_DATA_TABLE = "CREATE TABLE IF NOT EXISTS island_data (island_id VARCHAR(36) PRIMARY KEY, player_id VARCHAR(36))";
    private static final String CREATE_ISLAND_ID_TABLE = "CREATE TABLE IF NOT EXISTS island_id (player_id VARCHAR(36) PRIMARY KEY, island_id VARCHAR(36))";

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    @Override
    protected Map<SkyblockSQLQuery, String> getQueries() {
        return Map.of(
            SkyblockSQLQuery.FETCH_ISLAND_ID, FETCH_ISLAND_ID,
            SkyblockSQLQuery.FETCH_ISLAND_DATA, FETCH_ISLAND_DATA,
            SkyblockSQLQuery.DELETE_ISLAND_DATA, DELETE_ISLAND_DATA,
            SkyblockSQLQuery.DELETE_ISLAND_ID, DELETE_ISLAND_ID,
            SkyblockSQLQuery.SAVE_ISLAND_DATA, SAVE_ISLAND_DATA,
            SkyblockSQLQuery.SAVE_ISLAND_ID, SAVE_ISLAND_ID,
            SkyblockSQLQuery.CREATE_ISLAND_DATA_TABLE, CREATE_ISLAND_DATA_TABLE,
            SkyblockSQLQuery.CREATE_ISLAND_ID_TABLE, CREATE_ISLAND_ID_TABLE
        );
    }

    @Override
    protected Connection createConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, username, password);
        } catch (Exception expected) { // The driver will throw an exception if it fails to connect
            return null;
        }
    }

    @Override
    protected boolean enableDriver(ReadOnlyConfigurationSection properties) {
        host = properties.getString("host", "localhost");
        port = properties.getInt("port", 3306);
        database = properties.getString("database", "skyblock");
        username = properties.getString("username", "root");
        password = properties.getString("password", "password");

        try (Connection connection = createConnection()) {
            return connection != null;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "postgres";
    }
}
