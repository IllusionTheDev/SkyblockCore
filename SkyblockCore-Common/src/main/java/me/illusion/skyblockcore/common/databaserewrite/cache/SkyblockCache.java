package me.illusion.skyblockcore.common.databaserewrite.cache;

import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.databaserewrite.SkyblockDatabase;

public interface SkyblockCache extends SkyblockDatabase {

    CompletableFuture<Void> unloadServer();

}
