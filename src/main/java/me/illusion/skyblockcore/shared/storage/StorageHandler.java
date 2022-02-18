package me.illusion.skyblockcore.shared.storage;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageHandler {

    default CompletableFuture<Boolean> setup(File folder, Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> false);
    }

    CompletableFuture<Object> get(UUID uuid, String category);

    CompletableFuture<Void> save(UUID uuid, Object object, String category);

}
