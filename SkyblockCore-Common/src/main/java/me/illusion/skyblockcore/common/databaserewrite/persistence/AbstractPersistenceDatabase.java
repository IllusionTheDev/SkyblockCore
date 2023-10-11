package me.illusion.skyblockcore.common.databaserewrite.persistence;

import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;
import me.illusion.skyblockcore.common.databaserewrite.AbstractSkyblockDatabase;

public abstract class AbstractPersistenceDatabase extends AbstractSkyblockDatabase {

    protected AbstractPersistenceDatabase() {
        addTag(SkyblockDatabaseTag.FETCHING);
    }
}
