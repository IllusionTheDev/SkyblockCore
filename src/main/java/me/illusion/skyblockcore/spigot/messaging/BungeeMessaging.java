package me.illusion.skyblockcore.spigot.messaging;

import lombok.Getter;
import me.illusion.skyblockcore.shared.packet.Packet;
import me.illusion.skyblockcore.shared.packet.PacketHandler;
import me.illusion.skyblockcore.shared.packet.PacketProcessor;
import me.illusion.skyblockcore.shared.packet.impl.proxytoinstance.PacketDetermineServerInfo;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Collection;

/*
    Class responsible for Bungeecord <-> Server messaging

    When the first player joins the instance, the proxy sends the instance an identifier,
    which is used to identify what server is sending a message.

    Every time a player joins the instance, all loaded islands are sent to the proxy,
    as a list of island uuid's

 */
public class BungeeMessaging implements PluginMessageListener, PacketProcessor {

    private final SkyblockPlugin main;

    @Getter
    private String serverIdentifier;

    public BungeeMessaging(SkyblockPlugin main) {
        this.main = main;

        Bukkit.getMessenger().registerIncomingPluginChannel(main, "skyblock:channel", this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(main, "skyblock:channel");

        main.getPacketManager().subscribe(PacketDetermineServerInfo.class, new PacketHandler<PacketDetermineServerInfo>() {
            @Override
            public void onReceive(PacketDetermineServerInfo packet) {
                serverIdentifier = packet.getTargetServer();
            }
        });
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equalsIgnoreCase("skyblock:Channel"))
            return;

        main.getPacketManager().read(bytes);
    }


    @Override
    public void send(Packet packet) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (players.isEmpty())
            return;

        Player player = players.iterator().next();

        player.sendPluginMessage(main, "skyblock:Channel", packet.getAllBytes());
    }
}
