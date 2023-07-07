package me.illusion.skyblockcore.common.utilities.geometry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class Range implements Supplier<Integer> {

    private final int min;
    private final int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public Range(String range) {
        String[] split = range.split("-");

        if (split.length != 2) {
            if (range.matches("\\d+")) {
                min = Integer.parseInt(range);
                max = min;
                return;
            }

            throw new IllegalArgumentException("Invalid range: " + range);
        }

        min = Integer.parseInt(split[0]);
        max = Integer.parseInt(split[1]);
    }

    public boolean isInRange(int value) {
        return value >= min && value <= max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public Integer get() {
        if (min == max) // Sanity check
        {
            return min;
        }

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
