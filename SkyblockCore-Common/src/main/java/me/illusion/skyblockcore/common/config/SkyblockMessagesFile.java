package me.illusion.skyblockcore.common.config;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

public class SkyblockMessagesFile extends AbstractConfiguration {

    public SkyblockMessagesFile(SkyblockPlatform platform, String name) {
        super(platform, name + ".yml");
    }

    public void sendMessage(SkyblockAudience audience, String message) {

    }
}
