package me.illusion.skyblockcore.common.metrics;

import java.util.concurrent.CompletableFuture;

public interface SkyblockMetric {

    void addData(String key, Object value);

    CompletableFuture<Void> submit();

}
