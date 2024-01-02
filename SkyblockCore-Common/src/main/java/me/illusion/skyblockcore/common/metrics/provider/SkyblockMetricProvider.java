package me.illusion.skyblockcore.common.metrics.provider;

import me.illusion.skyblockcore.common.metrics.SkyblockMetric;
import me.illusion.skyblockcore.common.scheduler.ThreadContext;

public interface SkyblockMetricProvider {

    String getName();

    ThreadContext getThreadContext();

    void fillData(SkyblockMetric metric);

}
