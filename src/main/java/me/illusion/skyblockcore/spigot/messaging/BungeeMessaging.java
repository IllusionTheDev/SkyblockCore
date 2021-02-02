package me.illusion.skyblockcore.spigot.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Collection;
import java.util.UUID;

public class BungeeMessaging implements PluginMessageListener {

    private final SkyblockPlugin main;
    private String serverIdentifier;

    public BungeeMessaging(SkyblockPlugin main) {
        this.main = main;

        Bukkit.getMessenger().registerIncomingPluginChannel(main, "SkyblockChannel", this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(main, "SkyblockChannel");
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equalsIgnoreCase("SkyblockChannel"))
            return;

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);

        if (input.readUTF().equals("SkyblockChannelIdentifier"))
            serverIdentifier = input.readUTF();

    }

    public void sendData(Player player) {
        if (serverIdentifier == null)
            return;

        player.sendPluginMessage(main, "SkyblockChannel", prepareMessage());
    }

    private byte[] prepareMessage() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        Collection<UUID> islandIds = main.getIslandManager().getLoadedIslandIds();
        output.writeUTF("SkyblockChannel");

        output.writeInt(islandIds.size());
        output.writeUTF(serverIdentifier);
        output.writeBoolean(main.getIslandManager().isMaxCapacity());

        for (UUID uuid : islandIds)
            output.writeUTF(uuid.toString());

        return output.toByteArray();

    }

}
