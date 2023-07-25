package me.illusion.skyblockcore.common.database.fetching.sql;

/**
 * This enum is used for storing all the SQL queries that are used in the plugin.
 */
public enum SkyblockSQLQuery {

    FETCH_ISLAND_ID, // Fetch an island id from a player's uuid
    FETCH_ISLAND_DATA, // Fetch all the island data from an island id

    DELETE_ISLAND_DATA, // Deletes all the island data associated with an island id
    DELETE_ISLAND_ID, // Deletes all the island id associated with a player's uuid

    SAVE_ISLAND_DATA, // Saves all the island data associated with an island id
    SAVE_ISLAND_ID, // Saves all the island id associated with a player's uuid

    CREATE_ISLAND_DATA_TABLE, // Creates the island data table
    CREATE_ISLAND_ID_TABLE // Creates the island id table
}
