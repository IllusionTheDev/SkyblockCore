package me.illusion.skyblockcore.spigot.utilities.config;

import java.io.File;
import java.util.Map;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.section.WritableConfigurationSection;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A utility class for adapting Bukkit configuration classes to common configuration classes
 *
 * @see ConfigurationSection
 */
public final class BukkitConfigurationAdapter {

    private BukkitConfigurationAdapter() {

    }

    /**
     * Adapt a {@link ConfigurationSection} to a {@link ConfigurationSection}, flattening the section
     *
     * @param section The section to adapt
     * @return The adapted section
     */
    public static me.illusion.skyblockcore.common.config.section.ConfigurationSection adapt(File file, ConfigurationProvider provider,
        ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        Map<String, Object> map = section.getValues(false);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof ConfigurationSection) {
                map.put(key, adapt(file, provider, (ConfigurationSection) value));
            }
        }

        return new WritableConfigurationSection(map, file, provider);
    }

    public static void writeTo(me.illusion.skyblockcore.common.config.section.ConfigurationSection skyblockSection, ConfigurationSection bukkitSection) {
        for (String key : skyblockSection.getKeys()) {
            Object value = skyblockSection.get(key);

            if (value instanceof me.illusion.skyblockcore.common.config.section.ConfigurationSection) {
                ConfigurationSection section = bukkitSection.createSection(key);
                writeTo((me.illusion.skyblockcore.common.config.section.ConfigurationSection) value, section);
            } else {
                bukkitSection.set(key, value);
            }
        }
    }

}

/*
{mongo=ReadOnlyConfigurationSection{internalMap={host=localhost, port=27017, database=island, username=root, password=12345}, name='mongo'}, island-cache=ReadOnlyConfigurationSection{internalMap={type=memory}, name='island-cache'}, sqlite=ReadOnlyConfigurationSection{internalMap={name=island-storage/database}, name='sqlite'}, island=ReadOnlyConfigurationSection{internalMap={type=sqlite}, name='island'}, profile=ReadOnlyConfigurationSection{internalMap={type=sqlite}, name='profile'}, remote-sql=ReadOnlyConfigurationSection{internalMap={host=localhost, port=3306, database=island, username=root, password=12345}, name='remote-sql'}, redis=ReadOnlyConfigurationSection{internalMap={host=localhost, port=6379, password=12345}, name='redis'}}
 */