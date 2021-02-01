package me.illusion.skyblockcore.bungee;

import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.sql.SQLSerializer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerFinder {

    private final SkyblockBungeePlugin main;

    private final Map<UUID, String> islandsLoaded = new HashMap<>();

    public PlayerFinder(SkyblockBungeePlugin main) {
        this.main = main;
    }

    public void update(List<UUID> islandId, String server) {
        for (Map.Entry<UUID, String> entries : new HashSet<>(islandsLoaded.entrySet())) {
            UUID uuid = entries.getKey();
            String location = entries.getValue();

            if (location.equals(server) && !islandId.contains(uuid))
                islandsLoaded.remove(uuid);
        }

        for (UUID uuid : islandId)
            islandsLoaded.putIfAbsent(uuid, server);
    }

    public CompletableFuture<String> request(UUID member) {
        return SQLSerializer.deserialize(main.getMySQLConnection(), member, "PLAYER").handle((dataObject, thr) -> {
            if (thr != null)
                thr.printStackTrace();

            PlayerData data = (PlayerData) dataObject;

            if (data == null)
                return null;

            return islandsLoaded.get(data.getIslandId());

        });

    }
}
