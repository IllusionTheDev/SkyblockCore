package me.illusion.skyblockcore.common.databaserewrite;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public interface SkyblockDatabase {

    String getName();

    ReadOnlyConfigurationSection getProperties();

    CompletableFuture<Boolean> enable(SkyblockPlatform platform, ReadOnlyConfigurationSection properties);

    CompletableFuture<Void> flush();

    CompletableFuture<Void> wipe();

    Collection<SkyblockDatabaseTag> getTags();

}
