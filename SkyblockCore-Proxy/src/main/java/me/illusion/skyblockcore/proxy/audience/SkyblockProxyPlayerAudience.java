package me.illusion.skyblockcore.proxy.audience;

import java.util.UUID;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;

/**
 * Represents a Proxy's player audience.
 */
public interface SkyblockProxyPlayerAudience extends SkyblockAudience {

    /**
     * Gets the player's UUID.
     *
     * @return The player's UUID.
     */
    UUID getUniqueId();

    /**
     * Connects the player to a server.
     * @param server The server id to connect to.
     */
    void connect(String server);

}
