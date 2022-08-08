package me.illusion.skyblockcore.shared.storage;

import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.serialization.SkyblockSerializer;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class StorageUtils {


    public static byte[] getBytes(Object object) {
        try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
             ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput)) {

            objectOutput.writeObject(object);
            return byteOutput.toByteArray();
        } catch (Exception e) {
            ExceptionLogger.log(e);
            return null;
        }
    }

    public static Object getObject(byte[] bytes) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
             ObjectInputStream objectIn = new ObjectInputStream(byteIn)) {

            return objectIn.readObject();
        } catch (Exception e) {
            ExceptionLogger.log(e);
            return null;
        }
    }

    private static Map<String, Object> unflatten(Map<String, Object> originalMap) {
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // if the key starts with @, then get the section between @ and -

            if (key.startsWith("@")) {
                String section = key.substring(1, key.indexOf("-"));
                String key2 = key.substring(key.indexOf("-") + 1);

                Map<String, Object> sectionMap = (Map<String, Object>) originalMap.get(section);
                if (sectionMap == null) {
                    sectionMap = new HashMap<>();
                    originalMap.put(section, sectionMap);
                }

                sectionMap.put(key2, value);
                originalMap.remove(key);
            }
        }


        return originalMap;
    }

    public static Map<String, Object> process(SkyblockSerializable serializable) {
        Map<String, Object> map = SkyblockSerializer.serialize(serializable);
        serializable.save(map);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof SkyblockSerializable) {
                flatten(map, "@" + key, process((SkyblockSerializable) value));
            }

            if (!(value instanceof Serializable)) { // let's not save non-serializable stuff
                map.remove(key);
                System.err.println("Removed non-serializable value from storage: " + key);
                continue;
            }


        }

        map.put("classType", serializable.getClass().getName());

        return map;
    }

    /**
     * Flattens a map's contents into another map
     * the contents are identified by a key prefix
     *
     * @param targetMap the map to flatten into
     * @param mapKey    the key prefix
     * @param sourceMap the map to flatten from
     */
    public static void flatten(Map<String, Object> targetMap, String mapKey, Map<String, Object> sourceMap) {
        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof SkyblockSerializable) {
                flatten(targetMap, key, process((SkyblockSerializable) value));
            }

            targetMap.put(mapKey + "-" + key, value);
        }
    }

    public static SkyblockSerializable unserialize(Map<String, Object> map) {
        unflatten(map);

        String className = map.get("classType").toString();

        ReflectionFactory factory = ReflectionFactory.getReflectionFactory();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        SkyblockSerializable object = null;
        try {
            object = (SkyblockSerializable) factory.newConstructorForSerialization(clazz, Object.class.getConstructor()).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        SkyblockSerializer.load(object, map);
        object.load(map);
        return object;
    }
}
