package me.illusion.skyblockcore.shared.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class ObservableHashMap<K, V> extends HashMap<K, V> {

    private final Set<BiConsumer<K, V>> putEvents = new HashSet<>();
    private final Set<BiConsumer<K, V>> removeEvents = new HashSet<>();

    public ObservableHashMap() {

    }

    public ObservableHashMap(Map<K, V> map) {
        super.putAll(map);
    }

    public void onPut(BiConsumer<K, V> event) {
        putEvents.add(event);
    }

    public void onRemove(BiConsumer<K, V> event) {
        removeEvents.add(event);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean replaced = super.replace(key, oldValue, newValue);

        if (replaced)
            removeEvents.forEach(e -> e.accept(key, oldValue));

        return replaced;
    }

    @Override
    public V replace(K key, V value) {
        V val = super.replace(key, value);

        removeEvents.forEach(e -> e.accept(key, val));

        return val;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean removed = super.remove(key, value);

        if (removed)
            removeEvents.forEach(e -> e.accept((K) key, (V) value));

        return removed;
    }

    @Override
    public V remove(Object key) {
        V removed = super.remove(key);

        if (removed != null)
            removeEvents.forEach(e -> e.accept((K) key, removed));

        return removed;
    }

    @Override
    public V put(K key, V value) {
        putEvents.forEach(e -> e.accept(key, value));
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> otherMap) {
        putEvents.forEach(otherMap::forEach);
        super.putAll(otherMap);
    }
}
