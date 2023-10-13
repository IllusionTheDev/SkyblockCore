package me.illusion.skyblockcore.common.databaserewrite.registry.provider;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;
import me.illusion.skyblockcore.common.databaserewrite.registry.SkyblockDatabaseProvider;

public class MappedDatabaseProvider<T extends SkyblockDatabase> implements SkyblockDatabaseProvider<T> {

    private final Map<String, T> map;

    public MappedDatabaseProvider(Map<String, T> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    public MappedDatabaseProvider(T... databases) {
        Map<String, T> temp = new HashMap<>();

        for (T database : databases) {
            temp.put(database.getName(), database);
        }

        this.map = ImmutableMap.copyOf(temp);
    }

    @Override
    public T getDatabase(String name) {
        return map.get(name);
    }
}
