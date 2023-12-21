package me.illusion.skyblockcore.common.database.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.database.persistence.AbstractPersistenceDatabase;
import me.illusion.skyblockcore.common.database.sql.functional.ResultSetFunction;
import me.illusion.skyblockcore.common.database.sql.object.StatementObject;

public abstract class AbstractSQLPersistenceDatabase extends AbstractPersistenceDatabase {

    private final AtomicReference<Connection> connectionReference = new AtomicReference<>();

    protected AbstractSQLPersistenceDatabase() {
        addTag(SkyblockDatabaseTag.SQL);
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection properties) {
        setProperties(properties);

        return associate(() -> {
            Connection connection = getConnection();
            boolean valid = validateConnection(connection);

            if (!valid) {
                return false;
            }

            createTables();
            return true;
        });
    }

    protected <T> T runQuery(String query, List<StatementObject> list, ResultSetFunction<T> function) {
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            for (int index = 0; index < list.size(); index++) {
                list.get(index).applyTo(statement, index + 1);
            }

            ResultSet set = statement.executeQuery(); // This will be auto-closed by the try-with-resources

            try {
                return function.apply(set);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    protected <T> CompletableFuture<T> runQueryAsync(String query, List<StatementObject> list, ResultSetFunction<T> function) {
        return associate(() -> runQuery(query, list, function));
    }

    protected void runUpdate(String query, Consumer<PreparedStatement> consumer) {
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            consumer.accept(statement);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected void runUpdate(String query, StatementObject... objects) {
        this.runUpdate(query, statement -> {
            for (int index = 0; index < objects.length; index++) {
                try {
                    objects[index].applyTo(statement, index + 1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    protected void runUpdate(String query) {
        this.runUpdate(query, statement -> {
        });
    }

    protected CompletableFuture<Void> runUpdateAsync(String query, Consumer<PreparedStatement> consumer) {
        return associate(() -> {
            try (PreparedStatement statement = getConnection().prepareStatement(query)) {
                consumer.accept(statement);
                statement.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    protected CompletableFuture<Void> runUpdateAsync(String query, StatementObject... objects) {
        return runUpdateAsync(query, statement -> {
            for (int index = 0; index < objects.length; index++) {
                try {
                    objects[index].applyTo(statement, index + 1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SneakyThrows // This will never throw under normal circumstances
    protected Connection getConnection() {
        Connection current = connectionReference.get();

        if (!validateConnection(current)) {
            current = createConnection();
            connectionReference.set(current);
        }

        return current;
    }

    private boolean validateConnection(Connection connection) {
        if (connection == null) {
            return false;
        }

        try {
            return connection.isValid(1);
        } catch (Exception ignored) {
            // The driver will throw an exception if the parameter passed is below 0. This is not a problem.
            return false;
        }
    }

    protected abstract Connection createConnection();

    protected abstract Collection<String> getTables();

    protected abstract void createTables();

}
