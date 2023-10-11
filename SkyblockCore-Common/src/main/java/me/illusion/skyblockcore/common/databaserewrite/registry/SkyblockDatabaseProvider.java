package me.illusion.skyblockcore.common.databaserewrite.registry;

import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;

public interface SkyblockDatabaseProvider<T extends SkyblockDatabase> {

    T getDatabase(String name);

}
