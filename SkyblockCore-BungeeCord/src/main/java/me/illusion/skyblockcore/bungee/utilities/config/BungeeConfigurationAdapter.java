package me.illusion.skyblockcore.bungee.utilities.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import me.illusion.skyblockcore.common.config.ConfigurationProvider;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.config.section.WritableConfigurationSection;
import net.md_5.bungee.config.Configuration;

public final class BungeeConfigurationAdapter {

    private BungeeConfigurationAdapter() {

    }

    /**
     * Adapt a {@link Configuration} to a {@link ConfigurationSection}, flattening the section
     *
     * @param name          The name of the section, this parameter only exists because Bungee's configuration system does not store the name of the section
     * @param configuration The configuration to adapt
     * @return The adapted section
     */
    public static ConfigurationSection adapt(File file, ConfigurationProvider provider, String name, Configuration configuration) {
        if (configuration == null) {
            return null;
        }

        Map<String, Object> map = getValues(configuration);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Configuration) {
                map.put(key, adapt(file, provider, key, (Configuration) value));
            }
        }

        return new WritableConfigurationSection(name, map, file, provider);
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

    public static void writeTo(ConfigurationSection skyblockSection, Configuration configuration) {
        for (String key : skyblockSection.getKeys()) {
            Object value = skyblockSection.get(key);

            if (value instanceof ConfigurationSection section) {
                Configuration subSection = configuration.getSection(key);

                writeTo(section, subSection);
            } else {
                configuration.set(key, value);
            }
        }
    }
}
