package me.illusion.skyblockcore.bungee.data;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.bungee.comparison.AllocationType;
import me.illusion.skyblockcore.shared.data.IslandData;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.data.ServerInfo;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.instancetoproxy.PacketRespondIslandServer;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketRequestIslandServer;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerFinder {

    private static final String PROXY_ID = ProxyServer.getInstance().getName();
    private final Set<String> skyblockServerNames = new HashSet<>();
    private final Map<String, ServerInfo> serverInfoMap = new HashMap<>();

    private final Comparator<ServerInfo> comparator;

    private final SkyblockBungeePlugin main;

    public PlayerFinder(SkyblockBungeePlugin main) {
        this.main = main;

        Optional<AllocationType> optional = Enums.getIfPresent(AllocationType.class, main.getConfig().getString("island-allocation.type"));

        comparator = optional.or(AllocationType.EMPTY_PERCENTAGE).getComparator();

        main.getPacketManager().subscribe(PacketRespondIslandServer.class, new PacketHandler<PacketRespondIslandServer>() {
            @Override
            public void onReceive(PacketRespondIslandServer packet) {
                serverInfoMap.put(packet.getServerName(), packet.asServerInfo());
            }
        });
    }

    public String getAvailableServer() {
        List<Map.Entry<String, ServerInfo>> list = new ArrayList<>(serverInfoMap.entrySet());
        list.removeIf((entry) -> entry.getValue().getIslandCount() == entry.getValue().getIslandCapacity());

        if (list.isEmpty())
            return null;

        list.sort(Map.Entry.comparingByValue(comparator));

        return list.get(0).getKey();
    }

    public CompletableFuture<String> requestIslandServer(UUID uuid) {
        return getIslandId(uuid).thenApply(islandId -> {
            if (islandId == null) {
                // Player has no island, get random server
                return getAvailableServer();
            }

            // Player has island, get server

            Collection<UUID> members = getIslandMembers(islandId).join();
            CompletableFuture<String> future = new CompletableFuture<>();

            for (UUID member : members) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);

                if (player == null)
                    continue;

                String playerServer = player.getServer().getInfo().getName();

                if (!skyblockServerNames.contains(playerServer))
                    continue;

                PacketRequestIslandServer packet = new PacketRequestIslandServer(playerServer, islandId);
                main.getPacketManager().send(packet);

                CompletableFuture.runAsync(() -> {
                    PacketRespondIslandServer response = main.getPacketManager().await(PacketRespondIslandServer.class,
                            responsePacket -> responsePacket.getIslandId().equals(islandId),
                            3);

                    if (response == null || !response.isFound()) {
                        return;
                    }

                    future.complete(response.getServerName());
                });
            }

            try {
                return future.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                ExceptionLogger.log(e);
            }


            return getAvailableServer();
        });
    }

    public CompletableFuture<String> getLoadedIslandServer(UUID houseId) {
        if (houseId == null)
            return CompletableFuture.completedFuture(null);

        return CompletableFuture.supplyAsync(() -> {
            Collection<UUID> members = getIslandMembers(houseId).join();
            CompletableFuture<String> future = new CompletableFuture<>();

            for (UUID member : members) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);

                if (player == null)
                    continue;

                String playerServer = player.getServer().getInfo().getName();

                if (!skyblockServerNames.contains(playerServer))
                    continue;

                PacketRequestIslandServer packet = new PacketRequestIslandServer(playerServer, houseId);
                main.getPacketManager().send(packet);

                CompletableFuture.runAsync(() -> {
                    PacketRespondIslandServer response = main.getPacketManager().await(PacketRespondIslandServer.class,
                            responsePacket -> responsePacket.getIslandId().equals(houseId) && responsePacket.isFound(),
                            3);

                    if (response == null || !response.isFound()) {
                        return;
                    }

                    future.complete(response.getServerName());
                });
            }

            try {
                return future.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                ExceptionLogger.log(e);
            }

            return null;
        });
    }


    private CompletableFuture<UUID> getIslandId(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            StorageHandler handler = main.getStorageHandler();
            PlayerData data;
            try {
                data = (PlayerData) handler.get(uuid, "PLAYER").exceptionally((thr) -> null).get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                data = null;
            }

            if (data == null)
                return null;

            return data.getIslandId();
        });
    }

    private CompletableFuture<Collection<UUID>> getIslandMembers(UUID islandId) {
        return CompletableFuture.supplyAsync(() -> {
            StorageHandler handler = main.getStorageHandler();

            IslandData data;
            try {
                data = (IslandData) handler.get(islandId, "ISLAND").get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                data = null;
            }

            if (data == null)
                return null;

            return data.getUsers();
        });
    }


    private String getProxy(UUID uuid) {
        // TIL: Bungee doesn't have a main thread

        //if (.isPrimaryThread())
        //    throw new UnsafeSyncOperationException();

        if (!main.isMultiProxy()) {
            ProxyServer server = ProxyServer.getInstance();
            return server.getPlayer(uuid) == null ? null : PROXY_ID;
        }

        Jedis jedis = main.getJedisUtil().getJedis();

        byte[] bytes = jedis.get(uuid.toString().getBytes(StandardCharsets.UTF_8));

        jedis.close();
        main.getJedisUtil().getPool().returnResource(jedis);

        return new String(bytes);
    }

    public void registerServer(String name) {
        skyblockServerNames.add(name);
    }
}
