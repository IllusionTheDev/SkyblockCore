package me.illusion.skyblockcore.common.command.audience;

/**
 * Represents a console audience. This allows us to filter console-only commands.
 */
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
