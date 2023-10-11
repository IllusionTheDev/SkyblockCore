package me.illusion.skyblockcore.common.database;

public enum SkyblockDatabaseTag {

    // Database type

    FETCHING,
    CACHE,
    METRICS,

    // Implementation details

    FILE_BASED,
    SQL,
    NO_SQL,

    // Connection type

    REMOTE,
    LOCAL,


}
