package me.illusion.skyblockcore.common.registry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Registries {

    private final Collection<Registry<?>> internalCollection = ConcurrentHashMap.newKeySet();

    public <T extends Keyed> Registry<T> createRegistry(Class<T> type) {
        Registry<T> registry = new SimpleRegistry<>(type);
        internalCollection.add(registry);
        return registry;
    }

    public void registerRegistry(Registry<?> registry) {
        internalCollection.add(registry);
    }

    public void lockAll() {
        for (Registry<?> registry : internalCollection) {
            registry.lock();
        }
    }

    public <T extends Keyed> Registry<T> getRegistry(Class<T> clazz) {
        for (Registry<?> registry : internalCollection) {
            if (registry.getObjectType() == clazz) {
                return (Registry<T>) registry;
            }
        }

        return null;
    }

}
