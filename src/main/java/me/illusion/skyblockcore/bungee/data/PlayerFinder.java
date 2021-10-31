package me.illusion.skyblockcore.bungee.data;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.impl.proxy.proxy.request.PacketRequestServer;
import me.illusion.skyblockcore.shared.packet.impl.proxy.proxy.response.PacketRespondServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerFinder {

    private static final String PROXY_ID = ProxyServer.getInstance().getName();

    private final SkyblockBungeePlugin main;

    public PlayerFinder(SkyblockBungeePlugin main) {
        this.main = main;


        main.getPacketManager().subscribe(PacketRequestServer.class, new PacketHandler<PacketRequestServer>() {
            @Override
            public void onReceive(PacketRequestServer packet) {
                if (packet.getOriginProxy().equalsIgnoreCase(PROXY_ID))
                    return;

                UUID uuid = packet.getUuid();

                ProxyServer server = ProxyServer.getInstance();
                ProxiedPlayer player = server.getPlayer(uuid);

                if (player == null)
                    return;

                String result = player.getServer().getInfo().getName();

                PacketRespondServer response = new PacketRespondServer(uuid, PROXY_ID, packet.getOriginProxy(), result);

                main.getPacketManager().send(response);
            }
        });
    }

    public String getAvailableServer() {
        return null;
    }

    public CompletableFuture<String> request(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

            if (player == null || !player.isConnected()) {
                String proxy = getProxy(uuid);

                if (proxy == null)
                    return null;

                PacketRequestServer packet = new PacketRequestServer(uuid, PROXY_ID, proxy);

                main.getPacketManager().send(packet);

                PacketRespondServer response = main.getPacketManager().await(PacketRespondServer.class, (packetIn) -> packetIn.getUuid().equals(uuid));

                if (response == null)
                    return null;

                return response.getResultServer();

                // expect packet read, return packet value
            }

            return player.getServer().getInfo().getName();
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
