package me.illusion.skyblockcore.common.database.sql.object;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringStatementObject implements StatementObject {

    private final String value;

    public StringStatementObject(String value) {
        this.value = value;
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        statement.setString(index, value);
    }
}
