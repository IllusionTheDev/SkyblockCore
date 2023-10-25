package me.illusion.skyblockcore.common.database;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public interface SkyblockDatabase {

    String getName();

    ConfigurationSection getProperties();

    CompletableFuture<Boolean> enable(SkyblockPlatform platform, ConfigurationSection properties);

    CompletableFuture<Void> flush();

    CompletableFuture<Void> wipe();

    Collection<SkyblockDatabaseTag> getTags();

    default boolean hasTag(SkyblockDatabaseTag tag) {
        return getTags().contains(tag);
    }

}
