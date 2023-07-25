package me.illusion.skyblockcore.spigot.utilities.pdc;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A wrapper for any persistent data container, providing simpler set... and get... methods
 */
public class PDCWrapper { // ugly wrapper class because pdc bad

    private final JavaPlugin plugin;
    private final PersistentDataContainer pdc;

    public PDCWrapper(JavaPlugin plugin, PersistentDataContainer pdc) {
        this.plugin = plugin;
        this.pdc = pdc;
    }

    public void setString(String key, String value) {
        set(key, PersistentDataType.STRING, value);
    }

    public void setInteger(String key, int value) {
        set(key, PersistentDataType.INTEGER, value);
    }

    public void setDouble(String key, double value) {
        set(key, PersistentDataType.DOUBLE, value);
    }

    public void setFloat(String key, float value) {
        set(key, PersistentDataType.FLOAT, value);
    }

    public void setLong(String key, long value) {
        set(key, PersistentDataType.LONG, value);
    }

    public void setShort(String key, short value) {
        set(key, PersistentDataType.SHORT, value);
    }

    public void setByte(String key, byte value) {
        set(key, PersistentDataType.BYTE, value);
    }

    public void setBoolean(String key, boolean value) {
        set(key, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public String getString(String key) {
        return get(key, PersistentDataType.STRING);
    }

    public int getInteger(String key) {
        return get(key, PersistentDataType.INTEGER);
    }

    public double getDouble(String key) {
        return get(key, PersistentDataType.DOUBLE);
    }

    public float getFloat(String key) {
        return get(key, PersistentDataType.FLOAT);
    }

    public long getLong(String key) {
        return get(key, PersistentDataType.LONG);
    }

    public short getShort(String key) {
        return get(key, PersistentDataType.SHORT);
    }

    public byte getByte(String key) {
        return get(key, PersistentDataType.BYTE);
    }

    public boolean getBoolean(String key) {
        return get(key, PersistentDataType.BYTE) == 1;
    }

    public void remove(String key) {
        NamespacedKey namespacedKey = createKey(key);
        pdc.remove(namespacedKey);
    }

    public boolean has(String key) {
        NamespacedKey namespacedKey = createKey(key);
        return pdc.getKeys().contains(namespacedKey);
    }

    // utils

    protected NamespacedKey createKey(String name) {
        return new NamespacedKey(plugin, name);
    }

    protected <T> void set(String key, PersistentDataType<T, T> type, T value) {
        NamespacedKey namespacedKey = createKey(key);
        pdc.set(namespacedKey, type, value);
    }

    private <T> T get(String key, PersistentDataType<T, T> type) {
        return pdc.get(createKey(key), type);
    }

}
