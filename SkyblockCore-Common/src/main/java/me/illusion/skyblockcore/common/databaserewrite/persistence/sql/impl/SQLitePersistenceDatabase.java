package me.illusion.skyblockcore.common.databaserewrite.persistence.sql.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.databaserewrite.persistence.sql.AbstractSQLPersistenceDatabase;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.common.utilities.file.IOUtils;

public abstract class SQLitePersistenceDatabase extends AbstractSQLPersistenceDatabase {

    private File file;

    @Override
    public CompletableFuture<Boolean> enable(SkyblockPlatform platform, ReadOnlyConfigurationSection properties) {
        String name = properties.getString("name");
        File folder = platform.getConfigurationProvider().getDataFolder();

        return associate(() -> {
            File file = new File(folder, name + ".db");

            if (!file.exists()) {
                IOUtils.createFile(file);
            }

            this.file = file;
        }).thenCompose(value -> super.enable(properties));
    }

    @Override
    public String getName() {
        return "sqlite";
    }

    @Override
    public CompletableFuture<Void> wipe() {
        return associate(() -> {
            for (String table : getTables()) {
                runUpdate("DROP TABLE IF EXISTS " + table);
            }
        });
    }

    @Override
    protected Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
