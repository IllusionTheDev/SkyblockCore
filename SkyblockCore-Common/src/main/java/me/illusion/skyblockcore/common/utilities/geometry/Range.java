package me.illusion.skyblockcore.common.utilities.geometry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;

/**
 * Represents a range of numbers. If the min and max are the same, the range will always return that number.
 */
public class Range implements IntSupplier {

    private final int min;
    private final int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Parses a range from a string, e.g. "1-5" or "1"
     *
     * @param range The range string
     */
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

    /**
     * Checks if the value is in the range
     *
     * @param value The value
     * @return If the value is in the range
     */
    public boolean isInRange(int value) {
        return value >= min && value <= max;
    }

    /**
     * Gets the minimum value of the range
     *
     * @return The minimum value
     */
    public int getMin() {
        return min;
    }

    /**
     * Gets the maximum value of the range
     *
     * @return The maximum value
     */
    public int getMax() {
        return max;
    }

    @Override
    public int getAsInt() {
        if (min == max) // Sanity check
        {
            return min;
        }

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
