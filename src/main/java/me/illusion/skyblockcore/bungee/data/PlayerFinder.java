package me.illusion.skyblockcore.bungee.data;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.impl.proxy.proxy.request.PacketRequestServer;
import me.illusion.skyblockcore.shared.impl.proxy.proxy.response.PacketRespondServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class PlayerFinder {

    private final Map<UUID, CountDownLatch> latches = new HashMap<>();
    private final Map<UUID, PacketRespondServer> responses = new HashMap<>();

    private final SkyblockBungeePlugin main;

    public PlayerFinder(SkyblockBungeePlugin main) {
        this.main = main;
    }

    public String getAvailableServer() {
        return null;
    }

    public CompletableFuture<String> request(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

            if (player == null || !player.isConnected()) {
                PacketRequestServer packet = new PacketRequestServer(uuid, ProxyServer.getInstance().getName());

                main.getPacketManager().send(packet);

                CountDownLatch latch = new CountDownLatch(1);

                latches.put(uuid, latch);

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                PacketRespondServer response = responses.getOrDefault(uuid, null);

                if (response == null)
                    return null;

                // expect packet read, return packet value
            }

            return player.getServer().getInfo().getName();
        });
    }

    public void processResponse(PacketRespondServer packet) {
        notifyAll();
        latches.remove(packet.getUuid()).countDown();
        responses.put(packet.getUuid(), packet);
    }
}
