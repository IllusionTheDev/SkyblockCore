package me.illusion.skyblockcore.spigot.data;

import org.bukkit.entity.Player;

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
