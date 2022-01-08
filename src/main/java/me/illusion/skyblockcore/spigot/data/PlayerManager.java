package me.illusion.skyblockcore.spigot.data;

import me.illusion.skyblockcore.shared.utilities.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, SkyblockPlayer> players = new HashMap<>();

    /**
     * Registers a Skyblock player
     *
     * @param uuid   - The player UUID
     * @param player - the SkyblockPlayer object
     */
    void register(UUID uuid, SkyblockPlayer player) {
        players.put(uuid, player);
    }

    /**
     * Unregisters a SkyblockPlayer
     *
     * @param uuid - The player UUID
     */
    public void unregister(UUID uuid) {
        World world = Bukkit.getWorld("world");
        File folder = world.getWorldFolder();

        File dataFile = new File(folder + File.separator + "playerdata" + File.separator, uuid.toString() + ".dat");
        FileUtils.delete(dataFile);

        players.remove(uuid);
    }

    /**
     * Obtains a SkyblockPlayer
     *
     * @param uuid - The player UUID
     * @return SkyblockPlayer object, null if invalid
     */
    public SkyblockPlayer get(UUID uuid) {
        return players.get(uuid);
    }

    /**
     * Obtains a SkyblockPlayer
     *
     * @param player - The Bukkit player object
     * @return SkyblockPlayer object, null if invalid
     */
    public SkyblockPlayer get(Player player) {
        return get(player.getUniqueId());
    }
}
