package me.illusion.skyblockcore.common.utilities.file;

import java.io.File;
import java.util.function.Consumer;

public final class IOUtils {

    private IOUtils() {

    }

    public static void traverseAndLoad(File folder, Consumer<File> consumer) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            return;
        }

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                traverseAndLoad(file, consumer);
            } else {
                consumer.accept(file);
            }
        }
    }

    public static void createFile(File file) {
        if (file == null) {
            return;
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
