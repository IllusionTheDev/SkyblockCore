package me.illusion.skyblockcore.common.storage.profiles.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.database.persistence.sql.impl.SQLitePersistenceDatabase;
import me.illusion.skyblockcore.common.database.sql.object.StringStatementObject;
import me.illusion.skyblockcore.common.storage.profiles.SkyblockProfileStorage;

public class SQLiteProfileStorage extends SQLitePersistenceDatabase implements SkyblockProfileStorage {

    private static final String TABLE = "skyblock_profiles";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + "(player_id VARCHAR(36) PRIMARY KEY, profile_id VARCHAR(36))";
    private static final String GET_PROFILE_ID = "SELECT profile_id FROM " + TABLE + " WHERE player_id = ?";
    private static final String SET_PROFILE_ID = "INSERT INTO " + TABLE + " (player_id, profile_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE profile_id = ?";
    private static final String GET_ALL_PROFILE_IDS = "SELECT * FROM " + TABLE;

    @Override
    protected Collection<String> getTables() {
        return List.of(TABLE);
    }

    @Override
    protected void createTables() {
        runUpdate(CREATE_TABLE);
    }

    @Override
    public CompletableFuture<UUID> getProfileId(UUID playerId) {
        return runQueryAsync(GET_PROFILE_ID, List.of(new StringStatementObject(playerId.toString())), resultSet -> {
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("profile_id"));
            } else {
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> setProfileId(UUID playerId, UUID profileId) {
        return runUpdateAsync(SET_PROFILE_ID,
            new StringStatementObject(playerId.toString()),
            new StringStatementObject(profileId.toString()),
            new StringStatementObject(profileId.toString())
        );
    }

    @Override
    public CompletableFuture<Map<UUID, UUID>> getAllProfileIds() {
        return runQueryAsync(GET_ALL_PROFILE_IDS, List.of(), resultSet -> {
            Map<UUID, UUID> map = new ConcurrentHashMap<>();

            while (resultSet.next()) {
                map.put(UUID.fromString(resultSet.getString("player_id")), UUID.fromString(resultSet.getString("profile_id")));
            }

            return map;
        });
    }

    @Override
    public CompletableFuture<Void> setAllProfileIds(Map<UUID, UUID> profileIds) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (Map.Entry<UUID, UUID> entry : profileIds.entrySet()) {
            futures.add(setProfileId(entry.getKey(), entry.getValue()));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
