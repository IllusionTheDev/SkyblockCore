package me.illusion.skyblockcore.shared.sql;

public class SQLOperation {

    public static final String CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS PLAYER (serialized_id VARCHAR(36) NOT NULL, object_name varchar(80) default NULL, serialized_object blob, PRIMARY KEY (serialized_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
    public static final String CREATE_ISLAND_DATA_TABLE = "CREATE TABLE IF NOT EXISTS ISLAND (serialized_id VARCHAR(36) NOT NULL, object_name varchar(80) default NULL, serialized_object mediumblob, PRIMARY KEY (serialized_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    public static final String SQL_SERIALIZE_OBJECT = "INSERT INTO ?(object_name, serialized_object) VALUES (?, ?)";
    public static final String SQL_DESERIALIZE_OBJECT = "SELECT serialized_object FROM ? WHERE serialized_id = ?";

}
