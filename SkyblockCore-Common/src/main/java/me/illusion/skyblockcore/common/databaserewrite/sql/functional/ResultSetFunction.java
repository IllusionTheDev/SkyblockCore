package me.illusion.skyblockcore.common.databaserewrite.sql.functional;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetFunction<T> {

    T apply(ResultSet resultSet) throws Exception;

}
