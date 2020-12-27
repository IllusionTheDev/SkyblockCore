package me.illusion.skyblockcore.island;

import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.data.SkyblockPlayer;
import me.illusion.utilities.storage.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class IslandManager {

    private final CorePlugin main;

    public IslandManager(CorePlugin main) {
        this.main = main;
    }

    /**
     * Gets the island passed a belonging location
     *
     * @param location - The location to match
     * @return NULL if no match is found, Island object otherwise
     */
    public Island getIslandAt(Location location) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(main.getPlayerManager()::get)
                .filter(p -> LocationUtil.locationBelongs(location, p.getIsland().getPointOne(), p.getIsland().getPointTwo()))
                .map(SkyblockPlayer::getIsland)
                .findFirst()
                .orElse(null);
    }

}
