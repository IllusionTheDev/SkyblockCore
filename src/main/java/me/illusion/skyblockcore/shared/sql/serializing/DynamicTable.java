package me.illusion.skyblockcore.shared.sql.serializing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DynamicTable {

    // The target of this class is to create a table that can be altered according to the needs of the plugin.
    // The table will be created if it does not exist.
    // The table will be altered if it does exist, but the columns are not.

    // The table will be created with the following columns:
    // - id (primary key)
    // - category (varchar)
    // All other columns will be variable depending on the type of data that is being stored.

    // The table will be altered with the following columns:
    // - id (primary key)
    // - category (varchar)
    // All other columns will be variable depending on the type of data that is being stored.

    private final String tableName;
    private final Map<String, SqlColumnType> columns = new HashMap<>();

    private final Map<String, SqlColumnType> dirtyColumns = new HashMap<>();

    public DynamicTable(String tableName) {
        this.tableName = tableName;
    }

    public String getCreationQuery() {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        query.append("serialized_id VARCHAR PRIMARY KEY, ");
        query.append("category VARCHAR");
        for (Map.Entry<String, SqlColumnType> entry : columns.entrySet()) {
            query.append(", ").append(entry.getKey()).append(" ").append(entry.getValue().name());
        }
        query.append(")");
        return query.toString();
    }

    public String getWriteQuery(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!columns.containsKey(entry.getKey())) {
                addColumn(entry.getKey(), SqlColumnType.getType(entry.getValue()));
            }
        }


        // we'll write the map's contents
        // column name = map key

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ").append(tableName).append(" (");
        query.append("serialized_id");
        for (Map.Entry<String, SqlColumnType> entry : columns.entrySet()) {
            query.append(", ").append(entry.getKey());
        }

        query.append(") VALUES (");
        query.append("?");
        for (Map.Entry<String, SqlColumnType> entry : columns.entrySet()) {
            query.append(", ").append(map.get(entry.getKey()));
        }

        query.append(")");
        return query.toString();
    }

    public void addColumn(String columnName, SqlColumnType columnType) {
        dirtyColumns.put(columnName, columnType);
    }

    public void adapt(Connection validConnection, String columnId, Object object) {
        SqlColumnType columnType = SqlColumnType.getType(object);

        if (columnType == null) {
            throw new IllegalArgumentException("Object is not serializable: " + object);
        }

        SqlColumnType matchingColumnType = columns.get(columnId);

        if (matchingColumnType == null) {
            addColumn(columnId, columnType);
        }

        modifyDirtyColumns(validConnection);
    }

    private void modifyDirtyColumns(Connection validConnection) {
        for (Map.Entry<String, SqlColumnType> entry : dirtyColumns.entrySet()) {
            String columnName = entry.getKey();
            SqlColumnType columnType = entry.getValue();

            SqlColumnType existingColumnType = columns.get(columnName);

            String operation = (existingColumnType == null) ? "ADD" : "MODIFY";

            try {
                /*
                MySQL 10+ changed from
                ALTER TABLE <name> MODIFY COLUMN <id> <type>
                to
                ALTER TABLE <name> MODIFY <id> <type>
                 */
                if (validConnection.getMetaData().getDatabaseMajorVersion() >= 10)
                    operation += " COLUMN";
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String query = "ALTER TABLE " + tableName + operation + " ? ?";

            try (PreparedStatement statement = validConnection.prepareStatement(query)) {
                statement.setString(1, columnName);
                statement.setString(2, columnType.name());

                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        columns.putAll(dirtyColumns);
        dirtyColumns.clear();
    }


}
