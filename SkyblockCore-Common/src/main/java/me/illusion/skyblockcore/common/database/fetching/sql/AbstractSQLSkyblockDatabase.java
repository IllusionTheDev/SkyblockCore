package me.illusion.skyblockcore.common.database.fetching.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.database.fetching.AbstractSkyblockFetchingDatabase;
import me.illusion.skyblockcore.common.database.fetching.SkyblockFetchingDatabase;

/**
 * The abstract sql implementation of {@link SkyblockFetchingDatabase}. Certain methods are left abstract to allow for different implementations, as queries may
 * differ. For example, Postgres uses BYTEA for binary data, while MySQL uses BLOB.
 */
public abstract class AbstractSQLSkyblockDatabase extends AbstractSkyblockFetchingDatabase {

    private final Set<CompletableFuture<?>> futures = ConcurrentHashMap.newKeySet();
    private final AtomicReference<Connection> connectionReference = new AtomicReference<>();

    protected AbstractSQLSkyblockDatabase() {
        addTag(SkyblockDatabaseTag.SQL);
    }

    @Override
    public CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties) {
        return associate(() -> enableDriver(properties)).thenCompose(unused -> createTables());
    }

    @Override
    public CompletableFuture<UUID> fetchIslandId(UUID profileId) {
        return associate(() -> {
            String query = getQueries().get(SkyblockSQLQuery.FETCH_ISLAND_ID);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, profileId.toString());

                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    return UUID.fromString(set.getString("island_id"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<IslandData> fetchIslandData(UUID islandId) {
        return associate(() -> {
            String query = getQueries().get(SkyblockSQLQuery.FETCH_ISLAND_DATA);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, islandId.toString());

                ResultSet set = statement.executeQuery();

                if (!(set.next())) {
                    return null;
                }

                String id = set.getString("island_id");
                String ownerId = set.getString("owner_id");

                return new IslandData(UUID.fromString(id), UUID.fromString(ownerId));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> saveIslandData(IslandData data) {
        return associate(() -> {
            String query1 = getQueries().get(SkyblockSQLQuery.SAVE_ISLAND_DATA);
            String query2 = getQueries().get(SkyblockSQLQuery.SAVE_ISLAND_ID);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                query1); PreparedStatement statement2 = connection.prepareStatement(query2)) {
                statement.setString(1, data.getIslandId().toString());
                statement.setString(2, data.getOwnerId().toString());

                statement2.setString(1, data.getOwnerId().toString());
                statement2.setString(2, data.getIslandId().toString());

                statement.execute();
                statement2.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteIslandData(UUID islandId) {
        return associate(() -> {
            String query1 = getQueries().get(SkyblockSQLQuery.DELETE_ISLAND_DATA);
            String query2 = getQueries().get(SkyblockSQLQuery.DELETE_ISLAND_ID);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                query1); PreparedStatement statement2 = connection.prepareStatement(query2)) {
                statement.setString(1, islandId.toString());
                statement2.setString(1, islandId.toString());

                statement.execute();
                statement2.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> setProfileId(UUID playerId, UUID profileId) {
        return associate(() -> {
            String query = getQueries().get(SkyblockSQLQuery.SAVE_PLAYER_PROFILE);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, profileId.toString());
                statement.setString(2, playerId.toString());

                statement.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<UUID> getProfileId(UUID playerId) {
        return associate(() -> {
            String query = getQueries().get(SkyblockSQLQuery.FETCH_PLAYER_PROFILE);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerId.toString());

                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    return UUID.fromString(set.getString("profile_id"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Boolean> createTables() {
        return associate(() -> {
            String query = getQueries().get(SkyblockSQLQuery.CREATE_ISLAND_DATA_TABLE);
            String query2 = getQueries().get(SkyblockSQLQuery.CREATE_ISLAND_ID_TABLE);
            String query3 = getQueries().get(SkyblockSQLQuery.CREATE_PROFILE_TABLE);

            try (Connection connection = getConnection()) {
                return createTable(connection, query) && createTable(connection, query2) && createTable(connection, query3);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        });
    }

    private boolean createTable(Connection connection, String query) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Gets the queries for this database. The key is the query type, the value is the query itself.
     *
     * @return The queries for this database.
     */
    protected abstract Map<SkyblockSQLQuery, String> getQueries();

    /**
     * Creates a connection to the database. This method is called when the connection is invalid or null.
     *
     * @return The connection to the database.
     */
    protected abstract Connection createConnection();

    /**
     * Enables the driver for this database.
     *
     * @param properties The properties for this database.
     * @return If the driver was enabled successfully.
     */
    protected abstract boolean enableDriver(ReadOnlyConfigurationSection properties);

    /**
     * Gets the connection to the database. If the connection is invalid or null, it will create a new one.
     *
     * @return The connection to the database.
     */
    protected Connection getConnection() {
        Connection connection = connectionReference.get();

        try {
            if (connection == null || !connection.isValid(5)) {
                connection = createConnection();
            }
        } catch (SQLException ignored) {
            // The exception is thrown if the field passed in isValid is less than 0, which is not the case here
        }

        return connection;
    }

    private <T> CompletableFuture<T> associate(Supplier<T> supplier) {
        return registerFuture(CompletableFuture.supplyAsync(supplier));
    }

    private CompletableFuture<Void> associate(Runnable runnable) {
        return registerFuture(CompletableFuture.runAsync(runnable));
    }

    private <T> CompletableFuture<T> registerFuture(CompletableFuture<T> future) {
        future.thenRun(() -> futures.remove(future));
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        futures.add(future);
        return future;
    }

    protected Map<SkyblockSQLQuery, String> of(Object... objects) {
        Map<SkyblockSQLQuery, String> map = new EnumMap<>(SkyblockSQLQuery.class);

        for (int index = 0; index < objects.length; index += 2) {
            Object key = objects[index];
            Object value = objects[index + 1];

            if (!(key instanceof SkyblockSQLQuery)) {
                throw new IllegalArgumentException("Key must be of type SkyblockSQLQuery");
            }

            if (!(value instanceof String)) {
                throw new IllegalArgumentException("Value must be of type String");
            }

            map.put((SkyblockSQLQuery) key, (String) value);
        }

        return map;
    }
}
