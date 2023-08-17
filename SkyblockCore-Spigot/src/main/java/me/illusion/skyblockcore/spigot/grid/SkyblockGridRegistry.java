package me.illusion.skyblockcore.spigot.grid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.spigot.grid.impl.SingleWorldGridProvider;
import me.illusion.skyblockcore.spigot.grid.impl.WorldPerAreaGridProvider;

/**
 * Represents a registry for {@link SkyblockGridProvider}s, which are used to provide a grid for pasting islands.
 */
public class SkyblockGridRegistry {

    private final Map<String, SkyblockGridProvider> providers = new ConcurrentHashMap<>();

    public SkyblockGridRegistry() {
        registerDefaultProviders();
    }

    /**
     * Registers a provider.
     *
     * @param id       The id of the provider.
     * @param provider The provider.
     */
    public void registerProvider(String id, SkyblockGridProvider provider) {
        providers.put(id, provider);
    }

    /**
     * Registers the default providers.
     */
    private void registerDefaultProviders() {
        registerProvider("single-world", new SingleWorldGridProvider());
        registerProvider("world-per-area", new WorldPerAreaGridProvider());
    }

    /**
     * Obtains a provider by its id.
     *
     * @param id The id of the provider.
     * @return The provider.
     */
    public SkyblockGridProvider getProvider(String id) {
        return providers.get(id);
    }

}
