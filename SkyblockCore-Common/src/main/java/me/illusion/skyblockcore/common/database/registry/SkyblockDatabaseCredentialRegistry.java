package me.illusion.skyblockcore.common.database.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;

public class SkyblockDatabaseCredentialRegistry {

    private final Map<String, String> dependencies = new ConcurrentHashMap<>();
    private final Map<String, ConfigurationSection> credentialsMap = new ConcurrentHashMap<>();

    private final Map<String, List<String>> adjacencyList = new ConcurrentHashMap<>();


    public void registerCredentials(String name, ConfigurationSection credentials) {
        if (credentialsMap.containsKey(name)) {
            throw new IllegalStateException("Credentials " + name + " already registered, names are shared between files.");
        }

        credentialsMap.put(name, credentials);
    }

    public void registerDependency(String name, String dependency) {
        if (dependencies.containsKey(name)) {
            throw new IllegalStateException("Dependency " + name + " already registered");
        }

        dependencies.put(name, dependency);
        adjacencyList.computeIfAbsent(dependency, s -> new ArrayList<>()).add(name);
    }

    public Object get(String name) {
        String dependency = dependencies.get(name);

        if (dependency != null) {
            return get(dependency);
        }

        return credentialsMap.get(name);
    }

    public ConfigurationSection getCredentials(String name) {
        if (name == null) {
            return null;
        }

        ConfigurationSection section = credentialsMap.get(name);

        if (section == null) {
            return getCredentials(dependencies.get(name));
        }

        return section;
    }

    public void checkCyclicDependencies() {
        Set<String> visited = new HashSet<>();
        Set<String> currentlyChecking = new HashSet<>();
        Stack<String> cyclicPath = new Stack<>();

        for (String vertex : adjacencyList.keySet()) {
            if (hasCyclicDependency(vertex, visited, currentlyChecking, cyclicPath)) {
                throw new IllegalStateException("Cyclic dependency found: " + String.join(" -> ", cyclicPath));
            }
        }
    }

    private boolean hasCyclicDependency(String vertex, Set<String> visited, Set<String> currentlyChecking, Stack<String> cyclicPath) {
        if (currentlyChecking.contains(vertex)) {
            cyclicPath.push(vertex);
            return true; // Cyclic dependency found
        }

        if (visited.contains(vertex)) {
            return false; // Already visited, no cycle
        }

        visited.add(vertex);
        currentlyChecking.add(vertex);
        cyclicPath.push(vertex);

        List<String> neighbors = adjacencyList.get(vertex);
        if (neighbors != null) {
            for (String neighbor : neighbors) {
                if (hasCyclicDependency(neighbor, visited, currentlyChecking, cyclicPath)) {
                    return true; // Cyclic dependency found
                }
            }
        }

        currentlyChecking.remove(vertex);
        cyclicPath.pop();
        return false;
    }
}
