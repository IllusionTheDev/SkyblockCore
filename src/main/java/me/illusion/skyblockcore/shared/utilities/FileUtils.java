package me.illusion.skyblockcore.shared.utilities;

import java.io.File;

public final class FileUtils {

    private FileUtils() {
    }

    public static void delete(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        file.delete();
    }
}
