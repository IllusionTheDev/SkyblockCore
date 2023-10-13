package me.illusion.skyblockcore.common.storage.island.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.databaserewrite.persistence.sql.impl.MySQLPersistenceDatabase;
import me.illusion.skyblockcore.common.databaserewrite.sql.object.StringStatementObject;
import me.illusion.skyblockcore.common.storage.island.SkyblockIslandStorage;

public class MySQLIslandStorage extends MySQLPersistenceDatabase implements SkyblockIslandStorage { // TODO: De-duplicate the SQL storages

    private static final String PROFILES_TABLE = "island_profiles";
    private static final String ISLANDS_TABLE = "island_data";

    private static final String CREATE_PROFILES_TABLE = "CREATE TABLE IF NOT EXISTS " + PROFILES_TABLE + " ("
        + "profile_id VARCHAR(36) NOT NULL,"
        + "island_id VARCHAR(36) NOT NULL,"
        + "PRIMARY KEY (profile_id)"
        + ");";

    private static final String CREATE_ISLANDS_TABLE = "CREATE TABLE IF NOT EXISTS " + ISLANDS_TABLE + " ("
        + "island_id VARCHAR(36) NOT NULL,"
        + "owner_id VARCHAR(36) NOT NULL,"
        + "PRIMARY KEY (island_id)"
        + ");";

    private static final String GET_ISLAND_ID = "SELECT island_id FROM " + PROFILES_TABLE + " WHERE profile_id = ?;";
    private static final String GET_ISLAND_DATA = "SELECT * FROM " + ISLANDS_TABLE + " WHERE island_id = ?;";
    private static final String SAVE_ISLAND_DATA =
        "INSERT INTO " + ISLANDS_TABLE + " (island_id, owner_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE owner_id = ?;";
    private static final String DELETE_ISLAND_DATA = "DELETE FROM " + ISLANDS_TABLE + " WHERE island_id = ?;";
    private static final String GET_ALL_ISLAND_DATA = "SELECT * FROM " + ISLANDS_TABLE + ";";
    private static final String SAVE_ALL_ISLAND_DATA =
        "INSERT INTO " + ISLANDS_TABLE + " (island_id, owner_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE owner_id = ?;";

    @Override
    public CompletableFuture<UUID> getIslandId(UUID profileId) {
        return runQueryAsync(GET_ISLAND_ID, List.of(new StringStatementObject(profileId.toString())), resultSet -> {
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("island_id"));
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<IslandData> getIslandData(UUID islandId) {
        return runQueryAsync(GET_ISLAND_DATA, List.of(new StringStatementObject(islandId.toString())), resultSet -> {
            if (resultSet.next()) {
                return new IslandData(
                    UUID.fromString(resultSet.getString("island_id")),
                    UUID.fromString(resultSet.getString("owner_id"))
                );
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> saveIslandData(IslandData data) {
        return runUpdateAsync(SAVE_ISLAND_DATA,
            new StringStatementObject(data.getIslandId().toString()),
            new StringStatementObject(data.getOwnerId().toString()),
            new StringStatementObject(data.getOwnerId().toString())
        );
    }

    @Override
    public CompletableFuture<Void> deleteIslandData(UUID islandId) {
        return runUpdateAsync(DELETE_ISLAND_DATA, new StringStatementObject(islandId.toString()));
    }

    @Override
    public CompletableFuture<Collection<IslandData>> getAllIslandData() {
        return runQueryAsync(GET_ALL_ISLAND_DATA, List.of(), resultSet -> {
            try {
                Collection<IslandData> data = new ArrayList<>();

                while (resultSet.next()) {
                    data.add(new IslandData(
                        UUID.fromString(resultSet.getString("island_id")),
                        UUID.fromString(resultSet.getString("owner_id"))
                    ));
                }

                return data;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveAllIslandData(Collection<IslandData> data) {
        return associate(() -> {
            for (IslandData islandData : data) {
                runUpdate(SAVE_ALL_ISLAND_DATA,
                    new StringStatementObject(islandData.getIslandId().toString()),
                    new StringStatementObject(islandData.getOwnerId().toString()),
                    new StringStatementObject(islandData.getOwnerId().toString())
                );
            }
        });
    }

    @Override
    protected Collection<String> getTables() {
        return List.of(PROFILES_TABLE, ISLANDS_TABLE);
    }

    @Override
    protected void createTables() {
        runUpdate(CREATE_PROFILES_TABLE);
        runUpdate(CREATE_ISLANDS_TABLE);
    }
}
