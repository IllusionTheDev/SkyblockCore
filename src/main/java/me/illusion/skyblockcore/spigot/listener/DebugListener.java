package me.illusion.skyblockcore.spigot.listener;

import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Locale;

public class DebugListener implements Listener {

    private final SkyblockPlugin main;

    public DebugListener(SkyblockPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (!message.toLowerCase(Locale.ROOT).contains("wtf is going on"))
            return;

        Player player = event.getPlayer();

        player.sendMessage("Your location: " + formatLocation(player.getLocation()));
        player.sendMessage("Your island location: " + formatLocation(main.getPlayerManager().get(player).getIslandCenter()));
    }

    private String formatLocation(Location location) {
        return
                "World: " + location.getWorld().getName() + " | "
                        + "X: " + location.getX() + " | "
                        + "Y: " + location.getY() + " | "
                        + "Z: " + location.getZ();
    }
}
