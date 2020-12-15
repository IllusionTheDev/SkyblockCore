package me.illusion.skyblockcore.data;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, SkyblockPlayer> players = new HashMap<>();

    void register(UUID uuid, SkyblockPlayer player) {
        players.put(uuid, player);
    }

    public void unregister(UUID uuid) {
        players.remove(uuid);
    }

    public SkyblockPlayer get(UUID uuid) {
        return players.get(uuid);
    }

    public SkyblockPlayer get(Player player) {
        return get(player.getUniqueId());
    }
}
