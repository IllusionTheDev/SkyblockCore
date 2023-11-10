package me.illusion.skyblockcore.common.metrics;

import java.util.concurrent.CompletableFuture;
import me.illusion.skyblockcore.common.metrics.provider.SkyblockMetricProvider;

public interface SkyblockMetricBroker {

    SkyblockMetric createMetric(String name);

    void registerProvider(SkyblockMetricProvider provider);

    CompletableFuture<Void> submit(SkyblockMetric metric);

}
