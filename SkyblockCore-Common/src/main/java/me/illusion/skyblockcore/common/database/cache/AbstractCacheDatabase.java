package me.illusion.skyblockcore.common.database.cache;

import me.illusion.skyblockcore.common.database.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;

public abstract class AbstractCacheDatabase extends AbstractSkyblockDatabase implements SkyblockCache {

    protected AbstractCacheDatabase() {
        addTag(SkyblockDatabaseTag.CACHE);
    }
}
