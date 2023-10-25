package me.illusion.skyblockcore.common.database.sql.functional;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetConsumer {

    void accept(ResultSet resultSet) throws Exception;
}
