package me.illusion.skyblockcore.common.utilities.reflection;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectedConstructorCache {

    private final Map<Class<?>, ReflectedConstructor<?>> cache = new ConcurrentHashMap<>();
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();
    private final Set<String> knownDuds = ConcurrentHashMap.newKeySet();

    public <T> ReflectedConstructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        if (clazz == null) {
            return null;
        }

        classCache.putIfAbsent(clazz.getName(), clazz);
        return (ReflectedConstructor<T>) cache.computeIfAbsent(clazz, c -> new ReflectedConstructor<>(clazz, parameterTypes));
    }

    public <T> ReflectedConstructor<T> getConstructor(String className, Class<?>... parameterTypes) {
        return (ReflectedConstructor<T>) getConstructor(getClass(className), parameterTypes);
    }

    public Class<?> getClass(String className) {
        if (className == null) {
            return null;
        }

        if (knownDuds.contains(className)) {
            return null;
        }

        return classCache.computeIfAbsent(className, name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                if (knownDuds.add(name)) {
                    System.out.println("Failed to find class " + name);
                }
                return null;
            }
        });
    }

}
