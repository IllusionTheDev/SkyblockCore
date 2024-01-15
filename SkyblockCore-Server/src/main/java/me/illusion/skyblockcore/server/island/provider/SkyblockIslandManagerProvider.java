package me.illusion.skyblockcore.server.island.provider;

import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;

public interface SkyblockIslandManagerProvider {

    SkyblockIslandManager provideIslandManager(ConfigurationSection config);

    boolean canProvide();

}
