package me.illusion.skyblockcore.common.command.audience;

public abstract class SkyblockConsoleAudience implements SkyblockAudience {

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public boolean isConsole() {
        return true;
    }
}
