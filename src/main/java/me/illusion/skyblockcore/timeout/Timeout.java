package me.illusion.skyblockcore.timeout;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Timeout<T> {

    private final Cache<T, Long> cache;
    private final Class<T> clazz;

    public Timeout(int duration, Class<T> clazz) {
        cache = CacheBuilder.newBuilder().expireAfterWrite(duration, TimeUnit.SECONDS).build();
        this.clazz = clazz;
    }

    public long getSecondsLeft(T type) {
        Long result = cache.getIfPresent(type);

        return result == null ? 0 : result - Instant.now().getEpochSecond();
    }

    public void add(T type) {
        cache.put(type, Instant.now().getEpochSecond());
    }

    public boolean isInTimeout(T type) {
        return cache.getIfPresent(type) != null;
    }

    Class<T> getTypeClass() {
        return clazz;
    }
}
