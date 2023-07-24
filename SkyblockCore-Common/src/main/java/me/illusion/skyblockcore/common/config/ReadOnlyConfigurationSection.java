package me.illusion.skyblockcore.common.config;

import java.util.Map;

/**
 * Represents a read-only configuration section. This is similar to bukkit's system, but it is not tied to bukkit, and can be adapted to other systems, like
 * json
 */
public class ReadOnlyConfigurationSection {

    private final Map<String, Object> internalMap;
    private final String name;

    public ReadOnlyConfigurationSection(String name, Map<String, Object> internalMap) {
        this.name = name;
        this.internalMap = internalMap;
    }

    public String getName() {
        return name;
    }

    public boolean contains(String path) {
        return internalMap.containsKey(path);
    }

    public boolean isSet(String path) {
        return contains(path);
    }

    public ReadOnlyConfigurationSection getSection(String path) {
        return get(path, ReadOnlyConfigurationSection.class);
    }


    public Object get(String path) {
        return internalMap.get(path);
    }

    public Object get(String path, Object def) {
        return internalMap.getOrDefault(path, def);
    }

    public <T> T get(String path, Class<T> type) {
        Object obj = get(path);

        if (obj == null) {
            return null;
        }

        return type.cast(obj);
    }

    public <T> T get(String path, Class<T> type, T def) {
        Object obj = get(path, def);

        if (obj == null) {
            return null;
        }

        return type.cast(obj);
    }

    public String getString(String path) {
        return get(path, String.class);
    }

    public String getString(String path, String def) {
        return get(path, String.class, def);
    }

    public int getInt(String path) {
        return get(path, Integer.class, 0);
    }

    public int getInt(String path, int def) {
        return get(path, Integer.class, def);
    }

    public double getDouble(String path) {
        return get(path, Double.class, 0d);
    }

    public double getDouble(String path, double def) {
        return get(path, Double.class, def);
    }

    public boolean getBoolean(String path) {
        return get(path, Boolean.class, false);
    }

    public boolean getBoolean(String path, boolean def) {
        return get(path, Boolean.class, def);
    }

    public long getLong(String path) {
        return get(path, Long.class, 0L);
    }

    public long getLong(String path, long def) {
        return get(path, Long.class, def);
    }

    public float getFloat(String path) {
        return get(path, Float.class, 0f);
    }

}
