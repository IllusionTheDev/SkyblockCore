package me.illusion.skyblockcore.common.config.section;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;

public class WritableConfigurationSection extends MemoryConfigurationSection {

    private final File file;
    private final ConfigurationProvider provider;

    public WritableConfigurationSection(ConfigurationProvider provider, String fileName) {
        this.provider = provider;
        this.file = new File(provider.getDataFolder(), fileName);
    }

    public WritableConfigurationSection(String name, Map<String, Object> internalMap, File file, ConfigurationProvider provider) {
        super(name, internalMap);
        this.file = file;
        this.provider = provider;
    }

    public WritableConfigurationSection(File file, ConfigurationProvider provider) {
        this.file = file;
        this.provider = provider;
    }

    public WritableConfigurationSection(Map<String, Object> internalMap, File file, ConfigurationProvider provider) {
        super(internalMap);
        this.file = file;
        this.provider = provider;
    }

    public void addDefault(String path, Object value) {
        if (contains(path)) {
            return;
        }

        internalMap.put(path, value);
    }

    public void set(String path, Object value) {
        if (value instanceof ConfigurationSection section) {
            value = section.cloneWithName(path);
        }

        internalMap.put(path, value);
    }

    public void save() {
        provider.saveConfiguration(this, file);
    }

    @Override
    public ConfigurationSection cloneWithName(String name) {
        return new WritableConfigurationSection(name, new ConcurrentHashMap<>(internalMap), file, provider);
    }
}
