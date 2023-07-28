package me.illusion.skyblockcore.common.event.impl;

import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * This event is called when the SkyblockPlatform is enabled.
 */
public class SkyblockPlatformEnabledEvent extends SkyblockEvent {

    private final SkyblockPlatform platform;

    public SkyblockPlatformEnabledEvent(SkyblockPlatform plugin) {
        this.platform = plugin;
    }

    /**
     * Gets the SkyblockPlatform that was enabled.
     *
     * @return The SkyblockPlatform that was enabled.
     */
    public SkyblockPlatform getPlatform() {
        return platform;
    }
}
