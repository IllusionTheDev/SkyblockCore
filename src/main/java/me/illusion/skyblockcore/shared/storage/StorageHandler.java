package me.illusion.skyblockcore.shared.storage;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageHandler {

    default CompletableFuture<Boolean> setup(File folder) {
        return CompletableFuture.supplyAsync(() -> false);
    }

    default CompletableFuture<Boolean> setup(String ip, int port, String database, String username, String password) {
        return CompletableFuture.supplyAsync(() -> false);
    }

    boolean isFileBased();

    CompletableFuture<Object> get(UUID uuid, String category);

    CompletableFuture<Void> save(UUID uuid, Object object, String category);

}
