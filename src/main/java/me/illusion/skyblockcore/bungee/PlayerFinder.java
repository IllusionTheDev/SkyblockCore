package me.illusion.skyblockcore.bungee;

import lombok.Getter;
import me.illusion.skyblockcore.bungee.data.Server;
import me.illusion.skyblockcore.bungee.listener.RedisListener;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.sql.SQLSerializer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerFinder {

    private final SkyblockBungeePlugin main;

    @Getter
    private final Map<String, Server> servers = new HashMap<>();

    public PlayerFinder(SkyblockBungeePlugin main) {
        this.main = main;
    }

    public void update(List<UUID> islandId, String server, boolean available, boolean spigot) {
        RedisListener redisListener = main.getRedisListener();

        if (!servers.containsKey(server))
            servers.put(server, new Server(server, new ArrayList<>(), available));

        servers.get(server).setAvailable(available);

        for (Map.Entry<String, Server> entry : servers.entrySet()) {
            String name = entry.getKey();
            Server srv = entry.getValue();

            if (!server.equals(name))
                continue;

            List<UUID> islands = srv.getIslands();

            for (UUID uuid : new ArrayList<>(islands))
                if (!islandId.contains(uuid)) {
                    islands.remove(uuid);
                    if (main.isMultiProxy())
                        redisListener.remove(uuid, name);
                }
        }

        Server srv = servers.get(server);
        List<UUID> islands = srv.getIslands();

        for (UUID uuid : islandId) {
            islands.add(uuid);

            if (main.isMultiProxy())
                redisListener.register(uuid, server);
        }

        if (spigot)
            if (redisListener != null)
                redisListener.requestUpdate();
    }

    public void update(UUID islandId, String server) {
        servers.get(server).getIslands().add(islandId);

        RedisListener redisListener = main.getRedisListener();

        if (redisListener != null) {
            redisListener.register(islandId, server);
        }
    }

    public String getAvailableServer() {
        for (Map.Entry<String, Server> entry : servers.entrySet())
            if (entry.getValue().isAvailable())
                return entry.getKey();

        return null;
    }

    public CompletableFuture<String> request(UUID member) {
        return SQLSerializer.deserialize(main.getMySQLConnection(), member, "PLAYER").handle((dataObject, thr) -> {
            if (thr != null)
                thr.printStackTrace();

            PlayerData data = (PlayerData) dataObject;

            if (data == null)
                return null;

            UUID uuid = data.getIslandId();

            for (Map.Entry<String, Server> entry : servers.entrySet()) {
                String servername = entry.getKey();
                Server server = entry.getValue();

                if (server.getIslands().contains(uuid))
                    return servername;
            }

            return null;
        });

    }
}
