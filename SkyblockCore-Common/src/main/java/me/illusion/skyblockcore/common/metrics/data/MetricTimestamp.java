package me.illusion.skyblockcore.common.metrics.data;

import java.util.concurrent.TimeUnit;

public class MetricTimestamp {

    private final long time;
    private final TimeUnit timeUnit;

    public MetricTimestamp(long time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public static MetricTimestamp currentMillis() {
        return new MetricTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public static MetricTimestamp currentNano() {
        return new MetricTimestamp(System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    public long getTime() {
        return time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
