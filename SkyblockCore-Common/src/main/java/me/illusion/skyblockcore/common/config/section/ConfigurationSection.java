package me.illusion.skyblockcore.common.config.section;

import java.util.Collection;

public interface ConfigurationSection {

    boolean contains(String path);

    Object get(String path);

    Object get(String path, Object def);

    <T> T get(String path, Class<T> type);

    <T> T get(String path, Class<T> type, T def);

    String getString(String path);

    String getString(String path, String def);

    int getInt(String path);

    int getInt(String path, int def);

    double getDouble(String path);

    double getDouble(String path, double def);

    boolean getBoolean(String path);

    boolean getBoolean(String path, boolean def);

    long getLong(String path);

    long getLong(String path, long def);

    float getFloat(String path);

    float getFloat(String path, float def);

    ConfigurationSection getSection(String path);

    Collection<String> getKeys();


    ConfigurationSection cloneWithName(String name);

}
