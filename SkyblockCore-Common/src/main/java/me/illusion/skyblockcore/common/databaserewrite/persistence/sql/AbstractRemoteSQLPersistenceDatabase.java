package me.illusion.skyblockcore.common.databaserewrite.persistence.sql;

import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabaseTag;

public abstract class AbstractRemoteSQLPersistenceDatabase extends AbstractSQLPersistenceDatabase {

    protected String host;
    protected int port;
    protected String database;
    protected String username;
    protected String password;

    public AbstractRemoteSQLPersistenceDatabase() {
        addTag(SkyblockDatabaseTag.REMOTE);
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection properties) {
        host = properties.getString("host");
        port = properties.getInt("port");
        database = properties.getString("database");
        username = properties.getString("username");
        password = properties.getString("password");

        return super.enable(properties);
    }
}
