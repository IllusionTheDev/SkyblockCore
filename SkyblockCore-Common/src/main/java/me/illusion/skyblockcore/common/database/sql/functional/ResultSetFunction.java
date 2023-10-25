package me.illusion.skyblockcore.common.database.sql.functional;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetFunction<T> {

    T apply(ResultSet resultSet) throws Exception;

}
