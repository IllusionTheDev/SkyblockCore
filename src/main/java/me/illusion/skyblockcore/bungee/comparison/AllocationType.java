package me.illusion.skyblockcore.bungee.comparison;

import me.illusion.skyblockcore.shared.data.ServerInfo;

import java.util.Comparator;

public enum AllocationType {
    EMPTY(new EmptyFitComparator()),
    EMPTY_PERCENTAGE(new EmptyPercentageFitComparator()),
    FULL(new FullFitComparator()),
    FULL_PERCENTAGE(new FullPercentageFitComparator());

    private final Comparator<ServerInfo> comparator;

    AllocationType(Comparator<ServerInfo> comparator) {
        this.comparator = comparator;
    }

    public Comparator<ServerInfo> getComparator() {
        return comparator;
    }
}
