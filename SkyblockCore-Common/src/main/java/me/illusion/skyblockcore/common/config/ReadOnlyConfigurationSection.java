package me.illusion.skyblockcore.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        int index = path.indexOf('.');

        if (index != -1) {
            String key = path.substring(0, index);
            path = path.substring(index + 1);

            ReadOnlyConfigurationSection section = getSection(key);

            if (section == null) {
                return null;
            }

            return section.get(path);
        }

        return internalMap.get(path);
    }

    public Object get(String path, Object def) {
        Object obj = get(path);

        if (obj == null) {
            return def;
        }

        return obj;
    }

    public <T> T get(String path, Class<T> type) {
        Object obj = get(path);

        if (obj == null) {
            return null;
        }

        if (type.isInstance(obj) || type.isAssignableFrom(obj.getClass())) {
            return type.cast(obj);
        }

        throw new IllegalArgumentException("Cannot cast " + obj.getClass().getName() + " to " + type.getName() + " at " + path + " in " + name);
    }

    public <T> T get(String path, Class<T> type, T def) {
        Object obj = get(path, def);

        if (obj == null) {
            return def;
        }

        if (type.isInstance(obj) || type.isAssignableFrom(obj.getClass())) {
            return type.cast(obj);
        }

        throw new IllegalArgumentException("Cannot cast " + obj.getClass().getName() + " to " + type.getName());
    }

    public String getString(String path) {
        return get(path, String.class);
    }

    public String getString(String path, String def) {
        return get(path, String.class, def);
    }

    public Number getNumber(String path) {
        return get(path, Number.class);
    }

    public Number getNumber(String path, Number def) {
        return get(path, Number.class, def);
    }

    public int getInt(String path) {
        return getNumber(path).intValue();
    }

    public int getInt(String path, int def) {
        return getNumber(path, def).intValue();
    }

    public double getDouble(String path) {
        return getNumber(path).doubleValue();
    }

    public double getDouble(String path, double def) {
        return getNumber(path, def).doubleValue();
    }

    public boolean getBoolean(String path) {
        return get(path, Boolean.class, false);
    }

    public boolean getBoolean(String path, boolean def) {
        return get(path, Boolean.class, def);
    }

    public long getLong(String path) {
        return getNumber(path).longValue();
    }

    public long getLong(String path, long def) {
        return getNumber(path, def).longValue();
    }

    public float getFloat(String path) {
        return (float) getDouble(path);
    }

    public float getFloat(String path, float def) {
        return (float) getDouble(path, def);
    }

    public Collection<String> getKeys() {
        return new ArrayList<>(internalMap.keySet());
    }

    public List<String> getStringList(String path) {
        return get(path, List.class, new ArrayList<>());
    }

    public boolean isSection(String path) {
        return get(path) instanceof ReadOnlyConfigurationSection;
    }


}
