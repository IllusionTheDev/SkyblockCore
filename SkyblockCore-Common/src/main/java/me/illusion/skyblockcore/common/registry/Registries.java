package me.illusion.skyblockcore.common.registry;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Registries {

    private final Set<Registry<?>> registries = ConcurrentHashMap.newKeySet();

    public <T extends Keyed> Registry<T> createRegistry(Class<T> type) {
        Registry<T> registry = new SimpleRegistry<>(type);
        registries.add(registry);
        return registry;
    }

    public void registerRegistry(Registry<?> registry) {
        registries.add(registry);
    }

    public void lockAll() {
        for (Registry<?> registry : registries) {
            registry.lock();
        }
    }

    public <T extends Keyed> Registry<T> getRegistry(Class<T> clazz) {
        for (Registry<?> registry : registries) {
            if (registry.getObjectType() == clazz) {
                return (Registry<T>) registry;
            }
        }

        return null;
    }

}
