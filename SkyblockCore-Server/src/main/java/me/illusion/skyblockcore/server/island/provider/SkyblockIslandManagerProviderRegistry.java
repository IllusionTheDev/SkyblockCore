package me.illusion.skyblockcore.server.island.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;
import me.illusion.skyblockcore.server.config.IslandManagerConfiguration;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;

public class SkyblockIslandManagerProviderRegistry {

    private final Map<String, SkyblockIslandManagerProvider> providers = new ConcurrentHashMap<>();
    private final SkyblockServerPlatform serverPlatform;

    public SkyblockIslandManagerProviderRegistry(SkyblockServerPlatform serverPlatform) {
        this.serverPlatform = serverPlatform;
    }

    public void registerProvider(String name, SkyblockIslandManagerProvider provider) {
        providers.put(name, provider);
    }

    public SkyblockIslandManagerProvider getProvider(String name) {
        if (name == null) {
            return null;
        }

        return providers.get(name);
    }

    public Collection<SkyblockIslandManagerProvider> getAvailableProviders() {
        return List.copyOf(providers.values());
    }

    public Collection<SkyblockIslandManagerProvider> getValidProviders() {
        List<SkyblockIslandManagerProvider> availableProviders = new ArrayList<>(getAvailableProviders());
        availableProviders.removeIf(provider -> !provider.canProvide());
        return availableProviders;
    }

    public SkyblockIslandManager tryProvide() {
        IslandManagerConfiguration config = serverPlatform.getIslandManagerConfiguration();
        SkyblockIslandManagerProvider provider = getProvider(config.getProvider());

        if (provider == null) {
            return null;
        }

        if (!provider.canProvide()) {
            return null;
        }

        return provider.provideIslandManager(config.getProviderConfig());
    }

}
