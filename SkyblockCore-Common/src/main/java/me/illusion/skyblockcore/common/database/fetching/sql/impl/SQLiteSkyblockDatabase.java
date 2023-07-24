package me.illusion.skyblockcore.common.database.fetching.sql.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import me.illusion.skyblockcore.common.database.fetching.sql.AbstractSQLSkyblockDatabase;
import me.illusion.skyblockcore.common.database.fetching.sql.SkyblockSQLQuery;

public class SQLiteSkyblockDatabase extends AbstractSQLSkyblockDatabase {

    private static final String FETCH_ISLAND_ID = "SELECT island_id FROM skyblock_ids WHERE owner_id = ?";
    private static final String FETCH_ISLAND_DATA = "SELECT * FROM skyblock_data WHERE island_id = ?";
    private static final String DELETE_ISLAND_DATA = "DELETE FROM skyblock_data WHERE island_id";
    private static final String DELETE_ISLAND_ID = "DELETE FROM skyblock_ids WHERE island_id";
    private static final String SAVE_ISLAND_DATA = "INSERT OR REPLACE INTO skyblock_data (island_id, owner_id) VALUES (?, ?)";
    private static final String SAVE_ISLAND_ID = "INSERT OR REPLACE INTO skyblock_ids (owner_id, island_id) VALUES (?, ?)";
    private static final String CREATE_ISLAND_DATA_TABLE = "CREATE TABLE IF NOT EXISTS skyblock_data (island_id VARCHAR(36) PRIMARY KEY, owner_id VARCHAR(36))";
    private static final String CREATE_ISLAND_ID_TABLE = "CREATE TABLE IF NOT EXISTS skyblock_ids (owner_id VARCHAR(36) PRIMARY KEY, island_id VARCHAR(36))";

    private final File dataFolder;
    private File databaseFile;

    public SQLiteSkyblockDatabase(File dataFolder) {
        this.dataFolder = dataFolder;
    }

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
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        } catch (Exception expected) { // The driver will throw an exception if it fails to connect
            return null;
        }
    }

    @Override
    protected boolean enableDriver(Map<String, ?> properties) {
        String fileName = getOrDefault(properties, "file-name", "database");
        databaseFile = new File(dataFolder, fileName + ".db");

        try (Connection connection = getConnection()) {
            return connection.isValid(5);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return "sqlite";
    }

    @Override
    public boolean isFileBased() {
        return true;
    }
}
