package me.illusion.skyblockcore.common.databaserewrite;

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
