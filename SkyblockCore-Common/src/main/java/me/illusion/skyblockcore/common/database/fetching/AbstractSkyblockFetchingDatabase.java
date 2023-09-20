package me.illusion.skyblockcore.common.database.fetching;

import me.illusion.skyblockcore.common.database.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;

public abstract class AbstractSkyblockFetchingDatabase extends AbstractSkyblockDatabase implements SkyblockFetchingDatabase {

    protected AbstractSkyblockFetchingDatabase() {
        addTag(SkyblockDatabaseTag.FETCHING);
    }
}
