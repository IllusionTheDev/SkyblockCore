package me.illusion.skyblockcore.island;

import me.illusion.skyblockcore.CorePlugin;
import me.illusion.skyblockcore.data.SkyblockPlayer;
import me.illusion.utilities.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class IslandManager {

    private final CorePlugin main;

    public IslandManager(CorePlugin main) {
        this.main = main;
    }

    public Optional<Island> getIslandFromId(UUID islandId) {

        for (Player player : Bukkit.getOnlinePlayers()) {
            SkyblockPlayer sb = main.getPlayerManager().get(player);
            Island island = sb.getIsland();

            if (island != null && sb.getData().getIslandId().equals(islandId))
                return Optional.of(island);

        }
        return Optional.empty();
    }

    /**
     * Gets the island passed a belonging location
     *
     * @param location - The location to match
     * @return NULL if no match is found, Island object otherwise
     */
    public Island getIslandAt(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SkyblockPlayer sb = main.getPlayerManager().get(player);
            Island island = sb.getIsland();

            if (island == null)
                continue;

            if (LocationUtil.locationBelongs(location, island.getPointOne(), island.getPointTwo()))
                return island;
        }
        return null;
    }

}
