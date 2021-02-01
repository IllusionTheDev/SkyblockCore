package me.illusion.skyblockcore.spigot.island;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import me.illusion.skyblockcore.spigot.data.SkyblockPlayer;
import me.illusion.skyblockcore.spigot.utilities.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class IslandManager {

    private final SkyblockPlugin main;

    private final Map<UUID, Island> islands = new HashMap<>();

    public IslandManager(SkyblockPlugin main) {
        this.main = main;
    }

    void register(Island island) {
        islands.put(island.getData().getId(), island);
    }

    void unregister(Island island) {
        islands.remove(island.getData().getId());
    }

    public Optional<Island> getIslandFromId(UUID islandId) {
        return Optional.ofNullable(islands.get(islandId));
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
