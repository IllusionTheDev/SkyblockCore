package me.illusion.skyblockcore.common.database.cache;

import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.database.SkyblockDatabase;

public interface SkyblockCache extends SkyblockDatabase {

    CompletableFuture<Void> unloadServer();

}
