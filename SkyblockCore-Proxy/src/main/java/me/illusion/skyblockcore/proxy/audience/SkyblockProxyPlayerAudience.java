package me.illusion.skyblockcore.proxy.audience;

import java.util.UUID;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;

public interface SkyblockProxyPlayerAudience extends SkyblockAudience {

    UUID getUniqueId();

    void connect(String server);

}
