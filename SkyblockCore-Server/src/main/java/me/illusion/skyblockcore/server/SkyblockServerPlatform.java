package me.illusion.skyblockcore.server;

import me.illusion.skyblockcore.common.platform.SkyblockPlatform;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.player.SkyblockPlayerManager;

public interface SkyblockServerPlatform extends SkyblockPlatform {

    SkyblockIslandManager getIslandManager();

    SkyblockPlayerManager getPlayerManager();

}
