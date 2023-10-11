package me.illusion.skyblockcore.common.databaserewrite.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.databaserewrite.persistence.AbstractPersistenceDatabase;

public abstract class AbstractSQLPersistenceDatabase extends AbstractPersistenceDatabase {

    protected AbstractSQLPersistenceDatabase() {
        addTag(SkyblockDatabaseTag.SQL);
    }

    protected ResultSet runQuery(String query, Consumer<PreparedStatement> consumer) {
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            consumer.accept(statement);
            return statement.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected void runUpdate(String query, Consumer<PreparedStatement> consumer) {
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            consumer.accept(statement);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Connection getConnection() {
        return null; // TODO: write this
    }
}
