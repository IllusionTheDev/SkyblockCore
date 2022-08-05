package me.illusion.skyblockcore.shared.storage;

import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageHandler {

    default CompletableFuture<Boolean> setup(File folder, Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> false);
    }

    CompletableFuture<SkyblockSerializable> get(UUID uuid, String category);

    CompletableFuture<Void> save(UUID uuid, SkyblockSerializable object, String category);

    CompletableFuture<Void> delete(UUID uuid, String category);

    default Map<String, Object> process(SkyblockSerializable serializable) {
        Map<String, Object> map = new HashMap<>();

        serializable.save(map);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!(value instanceof Serializable)) { // let's not save non-serializable stuff
                map.remove(key);
                System.err.println("Removed non-serializable value from storage: " + key);
                continue;
            }

            if (value instanceof SkyblockSerializable) {
                flatten(map, "@" + key, process((SkyblockSerializable) value));
            }
        }

        map.put("classType", serializable.getClass().getName());

        return map;
    }

    /**
     * Flattens a map's contents into another map
     * the contents are identified by a key prefix
     *
     * @param targetMap the map to flatten into
     * @param mapKey    the key prefix
     * @param sourceMap the map to flatten from
     */
    default void flatten(Map<String, Object> targetMap, String mapKey, Map<String, Object> sourceMap) {
        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof SkyblockSerializable) {
                flatten(targetMap, key, process((SkyblockSerializable) value));
            }

            targetMap.put(mapKey + "-" + key, value);
        }
    }

}
