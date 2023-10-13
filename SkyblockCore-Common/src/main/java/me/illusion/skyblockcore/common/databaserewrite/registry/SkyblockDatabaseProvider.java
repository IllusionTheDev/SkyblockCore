package me.illusion.skyblockcore.common.databaserewrite.registry;

import java.util.Map;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;
import me.illusion.skyblockcore.common.databaserewrite.registry.provider.MappedDatabaseProvider;

public interface SkyblockDatabaseProvider<T extends SkyblockDatabase> {

    T getDatabase(String name);

    static SkyblockDatabaseProvider<?> of(Map<String, SkyblockDatabase> map) {
        return new MappedDatabaseProvider<>(map);
    }

    static SkyblockDatabaseProvider<?> of(SkyblockDatabase... databases) {
        return new MappedDatabaseProvider<>(databases);
    }

}
