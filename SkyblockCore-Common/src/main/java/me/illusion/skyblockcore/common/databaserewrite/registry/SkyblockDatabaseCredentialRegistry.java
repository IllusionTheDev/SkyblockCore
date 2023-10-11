package me.illusion.skyblockcore.common.databaserewrite.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.config.ReadOnlyConfigurationSection;

public class SkyblockDatabaseCredentialRegistry {

    private final Map<String, String> dependencies = new ConcurrentHashMap<>();
    private final Map<String, ReadOnlyConfigurationSection> credentialsMap = new ConcurrentHashMap<>();

    public void registerCredentials(String name, ReadOnlyConfigurationSection credentials) {
        credentialsMap.put(name, credentials);
    }

    public void registerDependency(String name, String dependency) {
        dependencies.put(name, dependency);
    }

    public ReadOnlyConfigurationSection getCredentials(String name) {
        if (name == null) {
            return null;
        }

        ReadOnlyConfigurationSection section = credentialsMap.get(name);

        if (section == null) {
            return getCredentials(dependencies.get(name));
        }

        return section;
    }

    public void checkCyclicDependencies() {
        Set<String> checked = new HashSet<>();
        Set<String> checking = new HashSet<>();

        for (String key : dependencies.keySet()) {
            if (hasCyclicDependency(key, checked, checking)) {
                throw new IllegalStateException(buildCyclicDependencyMessage(key, checking));
            }
        }
    }

    private boolean hasCyclicDependency(String key, Collection<String> checked, Collection<String> checking) {
        if (checked.contains(key)) {
            return false;
        }

        if (checking.contains(key)) {
            return true;
        }

        checking.add(key);

        String dependency = dependencies.get(key);

        if (dependency == null) {
            return false;
        }

        return hasCyclicDependency(dependency, checked, checking);
    }

    private String buildCyclicDependencyMessage(String key, Collection<String> checking) {
        StringBuilder builder = new StringBuilder();

        builder.append("Cyclic dependency found: ");

        for (String s : checking) {
            builder.append(s).append(" -> ");
        }

        builder.append(key);

        return builder.toString();
    }
}
