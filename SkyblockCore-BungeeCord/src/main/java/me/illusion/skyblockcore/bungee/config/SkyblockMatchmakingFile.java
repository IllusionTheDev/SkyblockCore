package me.illusion.skyblockcore.bungee.config;

import me.illusion.skyblockcore.bungee.utilities.storage.BungeeYMLBase;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class SkyblockMatchmakingFile extends BungeeYMLBase {

    private final String preferredComparator;

    public SkyblockMatchmakingFile(Plugin plugin) {
        super(plugin, "matchmaking.yml");

        Configuration configuration = getConfiguration();

        preferredComparator = configuration.getString("preferred-comparator", "least-islands");
    }

    public String getPreferredComparator() {
        return preferredComparator;
    }
}
