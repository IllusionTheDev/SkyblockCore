package me.illusion.skyblockcore.spigot.utilities.config;

import java.util.Map;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A utility class for adapting Bukkit configuration classes to common configuration classes
 *
 * @see ReadOnlyConfigurationSection
 */
public final class BukkitConfigurationAdapter {

    private BukkitConfigurationAdapter() {

    }

    /**
     * Adapt a {@link ConfigurationSection} to a {@link ReadOnlyConfigurationSection}, flattening the section
     *
     * @param section The section to adapt
     * @return The adapted section
     */
    public static ReadOnlyConfigurationSection adapt(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        Map<String, Object> map = section.getValues(true);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof ConfigurationSection) {
                map.put(key, adapt((ConfigurationSection) value));
            }
        }

        return new ReadOnlyConfigurationSection(section.getName(), map);
    }

}
