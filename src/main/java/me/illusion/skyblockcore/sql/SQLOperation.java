package me.illusion.skyblockcore.sql;

public class SQLOperation {

    public static final String SAVE_SERIALIZED = "INSERT INTO uuid_data(uuid, id) VALUES (?, ?)";
    public static final String GET_SERIALIZED = "SELECT id FROM uuid_data WHERE uuid = ?";
    public static final String LOAD_ISLAND = "SELECT id FROM island_data WHERE uuid = ?";
    public static final String SAVE_ISLAND = "INSERT INTO island_data(uuid, id) VALUES (?, ?)";

    public static final String CREATE_UUID_TABLE = "CREATE TABLE IF NOT EXISTS uuid_data (uuid VARCHAR(36), id LONG);";
    public static final String CREATE_ISLAND_TABLE = "CREATE TABLE IF NOT EXISTS island_data (uuid VARCHAR(36), id LONG);";
    public static final String CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS serialized_java_objects (serialized_id int(11) NOT NULL auto_increment, object_name varchar(80) default NULL, serialized_object mediumblob, PRIMARY KEY (serialized_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";


}
