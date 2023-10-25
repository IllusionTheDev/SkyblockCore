package me.illusion.skyblockcore.common.databaserewrite.registry.provider;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;
import me.illusion.skyblockcore.common.databaserewrite.registry.SkyblockDatabaseProvider;

public class MappedDatabaseProvider implements SkyblockDatabaseProvider {

    private final Map<String, SkyblockDatabase> map;

    public MappedDatabaseProvider(Map<String, SkyblockDatabase> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    public MappedDatabaseProvider(SkyblockDatabase... databases) {
        Map<String, SkyblockDatabase> temp = new HashMap<>();

        for (SkyblockDatabase database : databases) {
            temp.put(database.getName(), database);
        }

        this.map = ImmutableMap.copyOf(temp);
    }

    @Override
    public SkyblockDatabase getDatabase(String name) {
        return map.get(name);
    }
}
