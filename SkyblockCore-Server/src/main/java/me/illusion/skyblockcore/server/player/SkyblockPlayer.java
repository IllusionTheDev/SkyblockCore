package me.illusion.skyblockcore.server.player;

import java.util.UUID;
import me.illusion.skyblockcore.server.util.SkyblockLocation;

public interface SkyblockPlayer {

    String getName();

    UUID getUniqueId();

    UUID getSelectedProfileId();

    SkyblockLocation getLocation();

    void teleport(SkyblockLocation location);

}
