package me.illusion.skyblockcore.bungee;

import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.sql.SQLSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerFinder {

    private final SkyblockBungeePlugin main;

    private final Map<UUID, String> islandsLoaded = new HashMap<>();

    public PlayerFinder(SkyblockBungeePlugin main) {
        this.main = main;
    }

    public void update(UUID islandId, String server) {
        if (server == null)
            islandsLoaded.remove(islandId);
        else
            islandsLoaded.put(islandId, server);
    }

    public CompletableFuture<String> request(UUID member) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData playerData = (PlayerData) SQLSerializer.deserialize(main.getMySQLConnection(), member, "PLAYER");

            UUID islandId = playerData.getIslandId();

            return islandsLoaded.get(islandId);
        });
    }
}
