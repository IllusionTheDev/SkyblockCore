package me.illusion.skyblockcore.common.databaserewrite.persistence.sql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.databaserewrite.persistence.sql.AbstractRemoteSQLPersistenceDatabase;

public abstract class MySQLPersistenceDatabase extends AbstractRemoteSQLPersistenceDatabase {

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    protected Connection createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Properties properties = new Properties();

            properties.setProperty("user", username);
            properties.setProperty("password", password);
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");

            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public CompletableFuture<Void> wipe() {
        return associate(() -> {
            for (String table : getTables()) {
                runUpdate("DROP TABLE IF EXISTS " + table);
            }
        });
    }
}
