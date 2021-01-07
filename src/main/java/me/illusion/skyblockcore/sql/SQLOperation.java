package me.illusion.skyblockcore.sql;

public class SQLOperation {

    public static final String SAVE_PLAYER              = "INSERT INTO uuid_data(uuid, id) VALUES (?, ?) ON DUPLICATE KEY UPDATE uuid=VALUES(uuid), id=VALUES(id);";
    public static final String GET_PLAYER               = "SELECT id FROM uuid_data WHERE uuid = ?";
    
    public static final String GET_ISLAND               = "SELECT id FROM island_data WHERE uuid = ?";
    public static final String SAVE_ISLAND              = "INSERT INTO island_data(uuid, id) VALUES (?, ?) ON DUPLICATE KEY UPDATE uuid=VALUES(uuid), id=VALUES(id);";
    
    public static final String CREATE_UUID_TABLE        = "CREATE TABLE IF NOT EXISTS uuid_data (uuid VARCHAR(36), id LONG, PRIMARY KEY (uuid));";
    public static final String CREATE_ISLAND_TABLE      = "CREATE TABLE IF NOT EXISTS island_data (uuid VARCHAR(36), id LONG, PRIMARY KEY (uuid));";
    public static final String CREATE_DATA_TABLE        = "CREATE TABLE IF NOT EXISTS PLAYER (serialized_id int(11) NOT NULL auto_increment, object_name varchar(80) default NULL, serialized_object blob, PRIMARY KEY (serialized_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
    public static final String CREATE_ISLAND_DATA_TABLE = "CREATE TABLE IF NOT EXISTS ISLAND (serialized_id int(11) NOT NULL auto_increment, object_name varchar(80) default NULL, serialized_object mediumblob, PRIMARY KEY (serialized_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    static final String SQL_SERIALIZE_OBJECT            = "INSERT INTO ?(object_name, serialized_object) VALUES (?, ?)";
    static final String SQL_DESERIALIZE_OBJECT          = "SELECT serialized_object FROM ? WHERE serialized_id = ?";


}
