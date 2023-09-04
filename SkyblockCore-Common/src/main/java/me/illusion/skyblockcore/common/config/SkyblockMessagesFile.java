package me.illusion.skyblockcore.common.config;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.platform.SkyblockPlatform;

/**
 * Represents a generic messages file.
 */
public class SkyblockMessagesFile extends AbstractConfiguration {

    /**
     * Creates a new messages file.
     *
     * @param platform The platform this messages file is for.
     * @param name     The name of this messages file, without the extension.
     */
    public SkyblockMessagesFile(SkyblockPlatform platform, String name) {
        super(platform, name + ".yml");
    }

    /**
     * Sends a message to a SkyblockAudience.
     * @param audience The audience to send the message to.
     * @param message The message id to send.
     */
    public void sendMessage(SkyblockAudience audience, String message) {
        audience.sendMessage(configuration.getString(message));
    }
}
