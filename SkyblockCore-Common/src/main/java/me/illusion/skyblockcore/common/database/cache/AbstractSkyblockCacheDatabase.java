package me.illusion.skyblockcore.common.database.cache;

import me.illusion.skyblockcore.common.database.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;

public abstract class AbstractSkyblockCacheDatabase extends AbstractSkyblockDatabase implements SkyblockCacheDatabase {

    protected AbstractSkyblockCacheDatabase() {
        addTag(SkyblockDatabaseTag.CACHE);
    }
}
