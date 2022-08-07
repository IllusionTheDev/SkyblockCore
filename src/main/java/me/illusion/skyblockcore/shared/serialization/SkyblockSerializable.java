package me.illusion.skyblockcore.shared.serialization;

import java.io.Serializable;
import java.util.Map;

public interface SkyblockSerializable extends Serializable {

    default void load(Map<String, Object> map) {
        // Only override if you want to save additional data,
        // Fields that are not transient are automatically loaded.
    }

    default void save(Map<String, Object> map) {
        // Only override if you want to save additional data,
        // Fields that are not transient are automatically saved.
    }
}
