package me.illusion.skyblockcore.proxy.matchmaking.comparator;

import java.util.Collection;

/**
 * Represents a registry for {@link ServerDataComparator}s.
 */
public interface SkyblockServerComparatorRegistry {

    /**
     * Registers a comparator.
     *
     * @param comparator the comparator to register.
     */
    void registerComparator(ServerDataComparator comparator);

    /**
     * Unregisters a comparator.
     *
     * @param comparator the comparator to unregister.
     */
    void unregisterComparator(ServerDataComparator comparator);

    /**
     * Gets a comparator by its name.
     *
     * @param name the name of the comparator.
     * @return the comparator.
     */
    ServerDataComparator getComparator(String name);

    /**
     * Gets all registered comparators.
     *
     * @return all registered comparators.
     */
    Collection<ServerDataComparator> getComparators();

    /**
     * Gets the default comparator.
     *
     * @return the default comparator.
     */
    ServerDataComparator getDefaultComparator();

    /**
     * Sets the default comparator.
     *
     * @param comparator the default comparator.
     */
    void setDefaultComparator(ServerDataComparator comparator);
}
