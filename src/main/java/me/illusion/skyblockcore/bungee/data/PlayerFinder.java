package me.illusion.skyblockcore.bungee.data;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.data.PlayerData;
import me.illusion.skyblockcore.shared.storage.StorageHandler;
import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerFinder {

    private static final String PROXY_ID = ProxyServer.getInstance().getName();

    private final SkyblockBungeePlugin main;

    public PlayerFinder(SkyblockBungeePlugin main) {
        this.main = main;


    }

    public String getAvailableServer() {
        return null;
    }

    public CompletableFuture<String> requestIslandServer(UUID uuid) {
        return getIslandId(uuid).thenAccept(islandId -> {
            
        });
    }

    private CompletableFuture<UUID> getIslandId(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            StorageHandler handler = main.getStorageHandler();
            PlayerData data;
            try {
                data = (PlayerData) handler.get(uuid, "PLAYER").get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                data = null;
            }

            if (data == null)
                return null;

            return data.getIslandId();
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

}
