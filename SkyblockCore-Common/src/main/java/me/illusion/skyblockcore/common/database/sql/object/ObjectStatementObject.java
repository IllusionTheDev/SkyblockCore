package me.illusion.skyblockcore.common.database.sql.object;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ObjectStatementObject implements StatementObject {

    private final Object object;

    public ObjectStatementObject(Object object) {
        this.object = object;
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        statement.setObject(index, object);
    }
}
