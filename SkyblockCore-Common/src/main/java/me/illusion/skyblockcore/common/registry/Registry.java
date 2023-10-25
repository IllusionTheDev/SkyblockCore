package me.illusion.skyblockcore.common.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Registry<T extends Keyed> {

    protected final Map<SkyblockNamespacedKey, T> internalMap = new ConcurrentHashMap<>();
    protected boolean locked = false;

    public void register(T value) {
        if (locked) {
            throw new IllegalStateException("Registry is locked");
        }

        if (internalMap.containsKey(value.getKey())) {
            throw new IllegalArgumentException("Duplicate key " + value.getKey().toString());
        }

        internalMap.put(value.getKey(), value);
    }

    public T get(SkyblockNamespacedKey key) {
        return internalMap.get(key);
    }

    public void lock() {
        locked = true;
    }

    public abstract Class<T> getObjectType();
}
