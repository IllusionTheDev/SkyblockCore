package me.illusion.skyblockcore.spigot.player.audience;

import me.illusion.skyblockcore.common.command.audience.SkyblockConsoleAudience;
import org.bukkit.Bukkit;

/**
 * Represents a Bukkit console audience.
 */
public class SkyblockBukkitConsoleAudience extends SkyblockConsoleAudience {

    @Override
    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
