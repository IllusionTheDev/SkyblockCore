package me.illusion.skyblockcore.common.databaserewrite.sql.object;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementObject { // Class made because of how jdbc likes to throw exceptions everywhere resulting in a lot of try-catch blocks

    void applyTo(PreparedStatement statement, int index) throws SQLException;


}
