package me.illusion.skyblockcore.common.database.persistence;

import me.illusion.skyblockcore.common.database.AbstractSkyblockDatabase;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;

public abstract class AbstractPersistenceDatabase extends AbstractSkyblockDatabase {

    protected AbstractPersistenceDatabase() {
        addTag(SkyblockDatabaseTag.FETCHING);
    }
}
