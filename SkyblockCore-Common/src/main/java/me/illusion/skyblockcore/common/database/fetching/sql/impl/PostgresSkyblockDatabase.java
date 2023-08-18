package me.illusion.skyblockcore.common.database.fetching.sql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import me.illusion.skyblockcore.common.database.fetching.sql.AbstractRemoteSQLDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.AbstractSQLSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.SkyblockSQLQuery;

/**
 * The postgres implementation of {@link AbstractSQLSkyblockDatabase}
 */
public class PostgresSkyblockDatabase extends AbstractRemoteSQLDatabase {

    private static final String FETCH_ISLAND_ID = "SELECT island_id FROM island_id WHERE owner_id = ?";
    private static final String FETCH_ISLAND_DATA = "SELECT * FROM island_data WHERE island_id = ?";
    private static final String DELETE_ISLAND_DATA = "DELETE FROM island_data WHERE island_id = ?";
    private static final String DELETE_ISLAND_ID = "DELETE FROM island_id WHERE island_id = ?";
    private static final String SAVE_ISLAND_DATA = "INSERT INTO island_data (island_id, owner_id) VALUES (?, ?) ON CONFLICT (island_id) DO UPDATE SET owner_id = ?";
    private static final String SAVE_ISLAND_ID = "INSERT INTO island_id (owner_id, island_id) VALUES (?, ?) ON CONFLICT (owner_id) DO UPDATE SET island_id = ?";
    private static final String FETCH_PLAYER_PROFILE = "SELECT profile_id FROM profile WHERE player_id = ?";
    private static final String SAVE_PLAYER_PROFILE = "INSERT INTO profile (player_id, profile_id) VALUES (?, ?) ON CONFLICT (player_id) DO UPDATE SET profile_id = ?";
    private static final String CREATE_ISLAND_DATA_TABLE = "CREATE TABLE IF NOT EXISTS island_data (island_id VARCHAR(36) PRIMARY KEY, owner_id VARCHAR(36))";
    private static final String CREATE_ISLAND_ID_TABLE = "CREATE TABLE IF NOT EXISTS island_id (owner_id VARCHAR(36) PRIMARY KEY, island_id VARCHAR(36))";
    private static final String CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS profile (owner_id VARCHAR(36) PRIMARY KEY, profile_id VARCHAR(36))";

    @Override
    protected Map<SkyblockSQLQuery, String> getQueries() {
        return of(
            SkyblockSQLQuery.FETCH_ISLAND_ID, FETCH_ISLAND_ID,
            SkyblockSQLQuery.FETCH_ISLAND_DATA, FETCH_ISLAND_DATA,
            SkyblockSQLQuery.DELETE_ISLAND_DATA, DELETE_ISLAND_DATA,
            SkyblockSQLQuery.DELETE_ISLAND_ID, DELETE_ISLAND_ID,
            SkyblockSQLQuery.SAVE_ISLAND_DATA, SAVE_ISLAND_DATA,
            SkyblockSQLQuery.SAVE_ISLAND_ID, SAVE_ISLAND_ID,
            SkyblockSQLQuery.FETCH_PLAYER_PROFILE, FETCH_PLAYER_PROFILE,
            SkyblockSQLQuery.SAVE_PLAYER_PROFILE, SAVE_PLAYER_PROFILE,
            SkyblockSQLQuery.CREATE_ISLAND_DATA_TABLE, CREATE_ISLAND_DATA_TABLE,
            SkyblockSQLQuery.CREATE_ISLAND_ID_TABLE, CREATE_ISLAND_ID_TABLE,
            SkyblockSQLQuery.CREATE_PROFILE_TABLE, CREATE_PROFILE_TABLE
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
    public String getName() {
        return "postgres";
    }
}
