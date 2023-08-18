package me.illusion.skyblockcore.common.database.fetching.sql;

import java.sql.Connection;
import java.sql.SQLException;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;

public abstract class AbstractRemoteSQLDatabase extends AbstractSQLSkyblockDatabase {

    protected String host;
    protected int port;
    protected String database;
    protected String username;
    protected String password;

    @Override
    protected boolean enableDriver(ReadOnlyConfigurationSection properties) {
        host = properties.getString("host", "localhost");
        port = properties.getInt("port", 3306);
        database = properties.getString("database", "skyblock");
        username = properties.getString("username", "root");
        password = properties.getString("password", "password");

        try (Connection connection = createConnection()) {
            return connection != null && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }


}
