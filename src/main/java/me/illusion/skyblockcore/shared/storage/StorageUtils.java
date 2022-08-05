package me.illusion.skyblockcore.shared.storage;

import jdk.internal.reflect.ReflectionFactory;
import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
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

    public static SkyblockSerializable unserialize(Map<String, Object> map) {
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

        object.load(map);
        return object;
    }
}
