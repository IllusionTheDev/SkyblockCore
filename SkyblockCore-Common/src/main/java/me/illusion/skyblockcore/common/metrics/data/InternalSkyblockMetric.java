package me.illusion.skyblockcore.common.metrics.data;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.illusion.skyblockcore.common.metrics.SkyblockMetric;
import me.illusion.skyblockcore.common.metrics.SkyblockMetricBroker;

public class InternalSkyblockMetric implements SkyblockMetric {

    private final String name;
    private final MetricTimestamp timestamp;
    private final Map<String, Object> data = new ConcurrentHashMap<>();

    private final SkyblockMetricBroker broker;

    public InternalSkyblockMetric(SkyblockMetricBroker broker, String name, MetricTimestamp timestamp) {
        this.broker = broker;
        this.name = name;
        this.timestamp = timestamp;
    }

    @Override
    public void addData(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public CompletableFuture<Void> submit() {
        return broker.submit(this);
    }

    public Map<String, Object> getData() {
        return ImmutableMap.copyOf(data);
    }

    public String getName() {
        return name;
    }

    public MetricTimestamp getTimestamp() {
        return timestamp;
    }
}
