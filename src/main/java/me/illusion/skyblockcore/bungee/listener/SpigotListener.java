package me.illusion.skyblockcore.bungee.listener;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketProcessor;
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
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getTag().equals("SkyblockChannel"))
            return;

        main.getPacketManager().read(e.getData());
    }


    @Override
    public void send(Packet packet) {
        for (ServerInfo info : ProxyServer.getInstance().getServers().values())
            info.sendData("SkyblockChannel", packet.getAllBytes());
    }
}
