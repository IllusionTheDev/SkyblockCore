package me.illusion.skyblockcore.common.databaserewrite.cache;

import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.databaserewrite.AbstractSkyblockDatabase;

public abstract class AbstractCacheDatabase extends AbstractSkyblockDatabase {

    protected AbstractCacheDatabase() {
        addTag(SkyblockDatabaseTag.CACHE);
    }
}
