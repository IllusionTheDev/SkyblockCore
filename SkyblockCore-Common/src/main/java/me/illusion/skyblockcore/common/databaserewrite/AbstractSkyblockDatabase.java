package me.illusion.skyblockcore.common.databaserewrite;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public abstract class AbstractSkyblockDatabase implements SkyblockDatabase {

    private final Set<SkyblockDatabaseTag> tags = Sets.newConcurrentHashSet();
    private final Set<CompletableFuture<?>> futures = Sets.newConcurrentHashSet();

    private ConfigurationSection properties; // Only initialized on the enable method

    public void addTag(SkyblockDatabaseTag tag) {
        tags.add(tag);
    }

    @Override
    public ConfigurationSection getProperties() {
        return properties;
    }

    protected void setProperties(ConfigurationSection properties) {
        this.properties = properties;
    }

    @Override
    public Collection<SkyblockDatabaseTag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    protected <T> CompletableFuture<T> addFuture(CompletableFuture<T> future) {
        futures.add(future);
        return future.whenComplete((v, e) -> futures.remove(future));
    }

    protected <T> CompletableFuture<T> associate(Supplier<T> supplier) {
        return addFuture(CompletableFuture.supplyAsync(supplier));
    }

    protected CompletableFuture<Void> associate(Runnable runnable) {
        return addFuture(CompletableFuture.runAsync(runnable));
    }

    protected <T> CompletableFuture<T> associate(CompletableFuture<T> future) {
        return addFuture(future);
    }

    @Override
    public CompletableFuture<Boolean> enable(SkyblockPlatform platform, ConfigurationSection properties) {
        return enable(properties);
    }

    public CompletableFuture<Boolean> enable(ConfigurationSection properties) {
        throw new IllegalStateException("You must override at least one enable method!");
    }
}
