package me.illusion.skyblockcore.bungee.profile;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.common.profile.AbstractSkyblockProfileCache;

public class BungeeProfileCache extends AbstractSkyblockProfileCache { // Let's not do any caching here

    public BungeeProfileCache(SkyblockBungeePlugin plugin) {
        super(plugin.getDatabaseRegistry().getChosenDatabase());
    }

}
