package me.illusion.skyblockcore.common.database.fetching.sql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import me.illusion.skyblockcore.common.database.fetching.sql.AbstractSQLSkyblockDatabase;

/**
 * The mariadb implementation of {@link AbstractSQLSkyblockDatabase}
 */
public class MariaDBSkyblockDatabase extends MySQLSkyblockDatabase { // Same queries as MySQL

    @Override
    protected Connection createConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, username, password);
        } catch (Exception expected) { // The driver will throw an exception if it fails to connect
            return null;
        }
    }


    @Override
    public String getName() {
        return "mariadb";
    }
}
