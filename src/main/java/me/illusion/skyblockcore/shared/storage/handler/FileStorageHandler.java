package me.illusion.skyblockcore.shared.storage.handler;

import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.storage.SerializedFile;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.storage.file.DataFileUtils;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FileStorageHandler implements StorageHandler {

    protected File dataFolder;

    @Override
    public CompletableFuture<Boolean> setup(File folder, Map<String, Object> config) {
        this.dataFolder = new File(folder + File.separator + "data");
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Object> get(UUID uuid, String category) {
        Class<?> clazz = getClassByCategory(category);
        if (clazz == null) {
            return CompletableFuture.completedFuture(null);
        }

        File file = new File(dataFolder, category + "-" + uuid + "." + category.toLowerCase(Locale.ROOT));
        if (!file.exists()) {
            return CompletableFuture.completedFuture(null);
        }

        return DataFileUtils.getData(new SerializedFile(file), clazz);
    }

    @Override
    public CompletableFuture<Void> save(UUID uuid, Object object, String category) {
        Class<?> clazz = getClassByCategory(category);
        if (clazz == null) {
            return CompletableFuture.completedFuture(null);
        }

        File file = new File(dataFolder, category + "-" + uuid + "." + category.toLowerCase(Locale.ROOT));

        return CompletableFuture.runAsync(() -> {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    ExceptionLogger.log(e);
                }
            }

            DataFileUtils.saveData(file, object);
        });
    }

    protected Class<?> getClassByCategory(String category) {
        if (category.equalsIgnoreCase("player")) {
            return PlayerData.class;
        }

        if (category.equalsIgnoreCase("island")) {
            return IslandData.class;
        }

        return null;
    }
}
