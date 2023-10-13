package me.illusion.skyblockcore.common.databaserewrite.cache;

import me.illusion.skyblockcore.common.databaserewrite.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabaseTag;

public abstract class AbstractCacheDatabase extends AbstractSkyblockDatabase {

    protected AbstractCacheDatabase() {
        addTag(SkyblockDatabaseTag.CACHE);
    }
}
