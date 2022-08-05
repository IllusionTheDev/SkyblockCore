package me.illusion.skyblockcore.shared.sql;

public class SQLOperation {

    public static final String CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS PLAYER (serialized_id VARCHAR(36) NOT NULL, serialized_object blob, PRIMARY KEY (serialized_id));";
    public static final String CREATE_ISLAND_DATA_TABLE = "CREATE TABLE IF NOT EXISTS ISLAND (serialized_id VARCHAR(36) NOT NULL, serialized_object mediumblob, PRIMARY KEY (serialized_id));";

    public static final String SQL_DESERIALIZE_OBJECT = "SELECT serialized_object FROM ? WHERE serialized_id = ?";
    public static final String SQL_DELETE_OBJECT = "DELETE FROM ? WHERE serialized_id = ?";

}
