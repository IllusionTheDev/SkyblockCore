package me.illusion.skyblockcore.common.utilities.math;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Bounds {

    private final int min;
    private final int max;

    public Bounds(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

}
