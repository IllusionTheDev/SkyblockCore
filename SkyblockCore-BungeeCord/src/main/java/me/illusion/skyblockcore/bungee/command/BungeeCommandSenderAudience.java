package me.illusion.skyblockcore.bungee.command;

import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import net.md_5.bungee.api.CommandSender;

/**
 * Represents a SkyblockAudience for any generic BungeeCord CommandSender
 */
public class BungeeCommandSenderAudience implements SkyblockAudience {

    private final CommandSender sender;

    public BungeeCommandSenderAudience(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return true;
    }
}
