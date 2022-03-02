package me.illusion.skyblockcore.shared.storage;

import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
}
