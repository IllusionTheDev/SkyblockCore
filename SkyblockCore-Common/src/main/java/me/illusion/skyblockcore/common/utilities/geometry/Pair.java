package me.illusion.skyblockcore.common.utilities.geometry;

/**
 * A simple pair class
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class Pair<K, V> {

    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

}
