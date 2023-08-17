package me.illusion.skyblockcore.common.utilities.time;

import java.util.concurrent.TimeUnit;

/**
 * Represents a time value.
 */
public class Time {

    private final int duration;
    private final TimeUnit unit;

    public Time(int duration, TimeUnit unit) {
        this.duration = duration;
        this.unit = unit;
    }

    /**
     * Converts the time to the specified unit.
     *
     * @param unit The unit to convert to
     * @return The converted time
     */
    public long as(TimeUnit unit) {
        return unit.convert(duration, this.unit);
    }

    /**
     * Converts the time to ticks.
     *
     * @return The converted time
     */
    public long asTicks() {
        return as(TimeUnit.MILLISECONDS) / 50;
    }

    @Override
    public String toString() {
        return duration + " " + unit.name().toLowerCase();
    }

}
