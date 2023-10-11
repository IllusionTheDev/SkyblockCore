package me.illusion.skyblockcore.common.databaserewrite.persistence.sql;

import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;

public abstract class MySQLPersistenceDatabase extends AbstractSQLPersistenceDatabase {

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties) {
        return null;
    }
}
