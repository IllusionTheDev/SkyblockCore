package me.illusion.skyblockcore.shared.serialization;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SkyblockSerializer {

    private static final Map<Class<?>, Set<Field>> fields = new ConcurrentHashMap<>();

    public static Map<String, Object> serialize(Object object) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = object.getClass();

        Set<Field> cachedFields = fields.get(clazz);

        if (cachedFields == null) {
            cachedFields = getFields(clazz);
            fields.put(clazz, cachedFields);
        }


        for (Field field : cachedFields) {
            field.setAccessible(true);

            // if the field is not serializable, skip it


            try {
                map.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    public static void load(Object object, Map<String, Object> map) {
        Class<?> clazz = object.getClass();

        Set<Field> cachedFields = fields.get(clazz);

        if (cachedFields == null) {
            cachedFields = getFields(clazz);
            fields.put(clazz, cachedFields);
        }

        for (Field field : cachedFields) {
            field.setAccessible(true);
            try {
                field.set(object, map.get(field.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Set<Field> getFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null) {
            fields.addAll(getFields(superclass));
        }

        outer:
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            Class<?> fieldType = field.getType();
            for (Class<?> implementation : fieldType.getInterfaces()) {
                if (!implementation.isAssignableFrom(SkyblockSerializable.class) && !implementation.isAssignableFrom(Serializable.class)) {
                    continue outer;
                }
            }

            fields.add(field);
        }

        return fields;
    }
}
