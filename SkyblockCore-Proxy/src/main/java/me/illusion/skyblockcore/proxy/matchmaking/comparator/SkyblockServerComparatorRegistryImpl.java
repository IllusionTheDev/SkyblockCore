package me.illusion.skyblockcore.proxy.matchmaking.comparator;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.impl.HighestIslandCountComparator;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.impl.HighestPlayerCountComparator;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.impl.LowestIslandCountComparator;
import me.illusion.skyblockcore.proxy.matchmaking.comparator.impl.LowestPlayerCountComparator;

/**
 * Represents a simple implementation of {@link SkyblockServerComparatorRegistry}.
 */
public class SkyblockServerComparatorRegistryImpl implements SkyblockServerComparatorRegistry {

    private final Map<String, ServerDataComparator> comparators = new ConcurrentHashMap<>();
    private ServerDataComparator defaultComparator;

    public SkyblockServerComparatorRegistryImpl() {
        registerDefaults();
    }

    @Override
    public void registerComparator(ServerDataComparator comparator) {
        comparators.put(comparator.getName(), comparator);
    }

    @Override
    public void unregisterComparator(ServerDataComparator comparator) {
        comparators.remove(comparator.getName());
    }

    @Override
    public ServerDataComparator getComparator(String name) {
        return comparators.get(name);
    }

    @Override
    public Collection<ServerDataComparator> getComparators() {
        return comparators.values();
    }

    @Override
    public ServerDataComparator getDefaultComparator() {
        return defaultComparator;
    }

    @Override
    public void setDefaultComparator(ServerDataComparator comparator) {
        defaultComparator = comparator;
    }

    private void registerDefaults() {
        registerComparator(new HighestIslandCountComparator());
        registerComparator(new LowestIslandCountComparator());

        registerComparator(new HighestPlayerCountComparator());
        registerComparator(new LowestPlayerCountComparator());
    }

}
