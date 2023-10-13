package me.illusion.skyblockcore.common.storage;

import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;

public interface SkyblockStorage<T extends SkyblockDatabase> extends SkyblockDatabase {

    CompletableFuture<Void> migrateTo(T other);

}
