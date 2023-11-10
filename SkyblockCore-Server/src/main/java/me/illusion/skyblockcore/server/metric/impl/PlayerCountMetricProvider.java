package me.illusion.skyblockcore.server.metric.impl;

import me.illusion.skyblockcore.common.metrics.SkyblockMetric;
import me.illusion.skyblockcore.common.metrics.provider.SkyblockMetricProvider;
import me.illusion.skyblockcore.common.scheduler.ThreadContext;
import me.illusion.skyblockcore.server.SkyblockServerPlatform;

public class PlayerCountMetricProvider implements SkyblockMetricProvider {

    private final SkyblockServerPlatform platform;

    public PlayerCountMetricProvider(SkyblockServerPlatform platform) {
        this.platform = platform;
    }

    @Override
    public String getName() {
        return "player_count";
    }

    @Override
    public ThreadContext getThreadContext() {
        return ThreadContext.ASYNC;
    }

    @Override
    public void fillData(SkyblockMetric metric) {
        metric.addData("amount", platform.getPlayerManager().getPlayers().size());
    }
}
