package me.illusion.skyblockcore.bungee.utilities;

import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class StorageUtils {

    public static Map<String, Object> asMap(Configuration configuration) {
        Map<String, Object> map = new HashMap<>();

        for (String key : configuration.getKeys()) {
            map.put(key, configuration.get(key));
        }

        return map;
    }
}
