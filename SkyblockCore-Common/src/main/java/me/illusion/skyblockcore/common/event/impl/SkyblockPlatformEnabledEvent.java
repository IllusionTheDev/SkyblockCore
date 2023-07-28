package me.illusion.skyblockcore.common.event.impl;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public class SkyblockPlatformEnabledEvent extends SkyblockEvent {

    private final SkyblockPlatform platform;

    public SkyblockPlatformEnabledEvent(SkyblockPlatform plugin) {
        this.platform = plugin;
    }

    public SkyblockPlatform getPlatform() {
        return platform;
    }
}
