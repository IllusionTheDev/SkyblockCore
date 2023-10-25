package me.illusion.skyblockcore.common.utilities.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public final class IOUtils {

    public static final int BUFFER_SIZE = 1024;

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
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void copyFolder(SkyblockPlatform platform, File jarFile, String folderPath) {
        File dataFolder = platform.getConfigurationProvider().getDataFolder();
        File destination = new File(dataFolder, folderPath);

        if (!destination.exists()) {
            destination.mkdirs();
        }

        try (JarFile jar = new JarFile(jarFile)) {
            for (ZipEntry entry : Collections.list(jar.entries())) {
                String name = entry.getName();

                if (!name.startsWith(folderPath)) {
                    continue;
                }

                File file = new File(dataFolder, name);

                if (file.exists()) { // Don't overwrite
                    continue;
                }

                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                IOUtils.copy(jar.getInputStream(entry), file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream in, File file) throws IOException {
        IOUtils.createFile(file);

        try (OutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}
