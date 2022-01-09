package me.illusion.skyblockcore.bungee.listener;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketProcessor;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SpigotListener implements Listener, PacketProcessor {

    private final SkyblockBungeePlugin main;

    public SpigotListener(SkyblockBungeePlugin main) {
        this.main = main;
        ProxyServer.getInstance().getPluginManager().registerListener(main, this);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("SkyblockChannel"))
            return;

        main.getPacketManager().read(event.getData());
    }


    @Override
    public void send(Packet packet) {
        if (!(packet instanceof ProxyToServerPacket))
            return;

        ProxyToServerPacket proxyToServerPacket = (ProxyToServerPacket) packet;
        String targetServer = proxyToServerPacket.getTargetServer();
        byte[] bytes = packet.getAllBytes();

        if (targetServer == null) {
            for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values())
                serverInfo.sendData("SkyblockChannel", bytes);
        }
        ProxyServer
                .getInstance()
                .getServerInfo(proxyToServerPacket.getTargetServer())
                .sendData("SkyblockChannel", bytes);
    }
}
