package me.illusion.skyblockcore.common.databaserewrite;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;
import me.illusion.skyblockcore.common.database.SkyblockDatabaseTag;

public interface SkyblockDatabase {

    String getName();

    ReadOnlyConfigurationSection getProperties();

    CompletableFuture<Boolean> enable(ReadOnlyConfigurationSection properties);

    CompletableFuture<Void> flush();

    Collection<SkyblockDatabaseTag> getTags();

}
