package me.illusion.skyblockcore.spigot.grid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.spigot.grid.impl.SingleWorldGridProvider;
import me.illusion.skyblockcore.spigot.grid.impl.WorldPerAreaGridProvider;

public class SkyblockGridRegistry {

    private final Map<String, SkyblockGridProvider> providers = new ConcurrentHashMap<>();

    public SkyblockGridRegistry() {
        registerDefaultProviders();
    }

    public void registerProvider(String id, SkyblockGridProvider provider) {
        providers.put(id, provider);
    }

    public void registerDefaultProviders() {
        registerProvider("single-world", new SingleWorldGridProvider());
        registerProvider("world-per-area", new WorldPerAreaGridProvider());
    }

    public SkyblockGridProvider getProvider(String id) {
        return providers.get(id);
    }

}
