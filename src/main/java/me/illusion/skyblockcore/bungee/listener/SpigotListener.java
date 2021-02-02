package me.illusion.skyblockcore.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpigotListener implements Listener {

    private final SkyblockBungeePlugin main;

    public SpigotListener(SkyblockBungeePlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getTag().equals("SkyblockChannel"))
            return;

        ByteArrayDataInput input = ByteStreams.newDataInput(e.getData());

        if (!input.readUTF().equals("SkyblockChannel"))
            return;

        int size = input.readInt();
        String server = input.readUTF();
        boolean available = input.readBoolean();

        List<UUID> islandIds = new ArrayList<>();

        for (int i = 0; i < size; i++)
            islandIds.add(UUID.fromString(input.readUTF()));

        main.getPlayerFinder().update(islandIds, server, available, true);
    }
}
