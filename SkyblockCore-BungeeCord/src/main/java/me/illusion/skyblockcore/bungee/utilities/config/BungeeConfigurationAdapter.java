package me.illusion.skyblockcore.bungee.utilities.config;

import java.util.HashMap;
import java.util.Map;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import net.md_5.bungee.config.Configuration;

public class BungeeConfigurationAdapter {

    /**
     * Adapt a {@link Configuration} to a {@link ReadOnlyConfigurationSection}, flattening the section
     *
     * @param name          The name of the section, this parameter only exists because Bungee's configuration system does not store the name of the section
     * @param configuration The configuration to adapt
     * @return The adapted section
     */
    public static ReadOnlyConfigurationSection adapt(String name, Configuration configuration) {
        if (configuration == null) {
            return null;
        }

        Map<String, Object> map = getValues(configuration);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Configuration) {
                map.put(key, adapt(key, (Configuration) value));
            }
        }

        return new ReadOnlyConfigurationSection(name, map);
    }

    /**
     * Gets all values from a {@link Configuration}
     *
     * @param configuration The configuration to get the values from
     * @return A map of all values in the configuration
     */
    public static Map<String, Object> getValues(Configuration configuration) {
        Map<String, Object> map = new HashMap<>();

        for (String key : configuration.getKeys()) {
            map.put(key, configuration.get(key));
        }

        return map;
    }
}
