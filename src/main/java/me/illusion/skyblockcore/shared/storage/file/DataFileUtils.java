package me.illusion.skyblockcore.shared.storage.file;

import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.shared.storage.StorageUtils;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DataFileUtils {

    public static CompletableFuture<Object> getData(SerializedFile file, Class<?> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Object data = StorageUtils.getObject(file.getBytes());
                return clazz.cast(data);
            } catch (Exception e) {
                ExceptionLogger.log(e);
            }

            return null;
        });
    }

    public static void saveData(File file, Object data) {
        assureFileIsCreated(file);

        SerializedFile serialized = new SerializedFile(file, StorageUtils.getBytes(data));
        serialized.save();
    }

    private static void assureFileIsCreated(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                ExceptionLogger.log(e);
            }
        }
    }

}
