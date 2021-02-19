package me.illusion.skyblockcore.shared.storage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageHandler {

    CompletableFuture<Boolean> setup(String ip, int port, String database, String username, String password);

    CompletableFuture<Object> get(UUID uuid, String category);

    CompletableFuture<Void> save(UUID uuid, Object object, String category);

}
