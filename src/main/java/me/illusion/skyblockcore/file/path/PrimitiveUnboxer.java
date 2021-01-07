package me.illusion.skyblockcore.file.path;

import java.util.HashMap;
import java.util.Map;

public final class PrimitiveUnboxer {

    private PrimitiveUnboxer() {
        // Private empty constructor for utility class
    }

    private static final Map<Class<?>, Class<?>> primitives = new HashMap<>();

    static {
        primitives.put(Integer.class, int.class);
        primitives.put(Byte.class, byte.class);
        primitives.put(Boolean.class, boolean.class);
        primitives.put(Double.class, double.class);
        primitives.put(Short.class, short.class);
    }

    public static Class<?> unbox(Class<?> clazz) {
        return primitives.getOrDefault(clazz, clazz);
    }
}