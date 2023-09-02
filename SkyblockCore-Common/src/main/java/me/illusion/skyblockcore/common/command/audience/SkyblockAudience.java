package me.illusion.skyblockcore.common.command.audience;

public interface SkyblockAudience {

    void sendMessage(String message);

    boolean hasPermission(String permission);

    boolean isConsole();

}
