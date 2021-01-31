package me.illusion.skyblockcore.spigot.file.path;

public final class PrimitiveUnboxer {

    private PrimitiveUnboxer() {
        // Private empty constructor for utility class
    }

    public static Class<?> unbox(Class<?> clazz) {
        // Hardcoding the values for performance
        if (clazz == Integer.class)
            return int.class;
        if (clazz == Byte.class)
            return byte.class;
        if (clazz == Boolean.class)
            return boolean.class;
        if (clazz == Double.class)
            return double.class;
        if (clazz == Short.class)
            return short.class;

        return clazz;
    }
}