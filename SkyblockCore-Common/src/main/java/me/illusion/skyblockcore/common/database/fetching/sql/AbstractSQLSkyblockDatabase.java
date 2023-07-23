package me.illusion.skyblockcore.common.database.fetching.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import me.illusion.skyblockcore.common.data.IslandData;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;

public abstract class AbstractSQLSkyblockDatabase implements SkyblockDatabase {

    private final Set<CompletableFuture<?>> futures = ConcurrentHashMap.newKeySet();

    @Override
    public CompletableFuture<Boolean> enable(Map<String, ?> properties) {
        return associate(() -> enableDriver(properties)).thenCompose((__) -> createTables());
    }

    @Override
    public CompletableFuture<UUID> fetchIslandId(UUID playerId) {
        return associate(() -> {
            String query = getQueries().get(SkyblockSQLQuery.FETCH_ISLAND_ID);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerId.toString());

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
    public CompletableFuture<Void> flush() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Boolean> createTables() {
        return associate(() -> {
            String query = getQueries().get(SkyblockSQLQuery.CREATE_ISLAND_DATA_TABLE);
            String query2 = getQueries().get(SkyblockSQLQuery.CREATE_ISLAND_ID_TABLE);

            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                query); PreparedStatement statement2 = connection.prepareStatement(query2)) {
                statement.execute();
                statement2.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

            return true;
        });
    }

    protected abstract Map<SkyblockSQLQuery, String> getQueries();

    protected abstract Connection getConnection();

    protected abstract boolean enableDriver(Map<String, ?> properties);

    protected <T> T getOrDefault(Map<String, ?> map, String key, T defaultValue) {
        Object value = map.get(key);

        if (value == null) {
            return defaultValue;
        }

        return (T) value;
    }

    protected <T> T getOrDefault(Map<String, ?> map, String key) {
        return getOrDefault(map, key, null);
    }

    private <T> CompletableFuture<T> associate(Supplier<T> supplier) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier);

        future.thenRun(() -> futures.remove(future));
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        futures.add(future);
        return future;
    }

    private CompletableFuture<Void> associate(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable);

        future.thenRun(() -> futures.remove(future));
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        futures.add(future);
        return future;
    }
}
