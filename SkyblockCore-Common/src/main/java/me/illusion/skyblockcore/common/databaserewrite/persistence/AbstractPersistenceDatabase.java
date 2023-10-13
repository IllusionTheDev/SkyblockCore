package me.illusion.skyblockcore.common.databaserewrite.persistence;

import me.illusion.skyblockcore.common.databaserewrite.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabaseTag;

public abstract class AbstractPersistenceDatabase extends AbstractSkyblockDatabase {

    protected AbstractPersistenceDatabase() {
        addTag(SkyblockDatabaseTag.FETCHING);
    }
}
