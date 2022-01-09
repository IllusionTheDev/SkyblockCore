package me.illusion.skyblockcore.bungee.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectListener implements Listener {

    @EventHandler
    public void onConnect(ServerConnectedEvent event) {
        Server server = event.getServer();
        String serverName = server.getInfo().getName();

        server.sendData("SkyblockChannel", formMessage(serverName));
    }

    private byte[] formMessage(String serverName) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeUTF("SkyblockChannelIdentifier");
        output.writeUTF(serverName);

        return output.toByteArray();
    }
}
