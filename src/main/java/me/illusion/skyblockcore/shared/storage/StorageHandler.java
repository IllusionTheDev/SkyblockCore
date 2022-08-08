package me.illusion.skyblockcore.shared.storage;

import me.illusion.skyblockcore.shared.serialization.SkyblockSerializable;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageHandler {

    default CompletableFuture<Boolean> setup(File folder, Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> false);
    }

    CompletableFuture<SkyblockSerializable> get(UUID uuid, String category);

    CompletableFuture<Void> save(UUID uuid, SkyblockSerializable object, String category);

    CompletableFuture<Void> delete(UUID uuid, String category);


}
