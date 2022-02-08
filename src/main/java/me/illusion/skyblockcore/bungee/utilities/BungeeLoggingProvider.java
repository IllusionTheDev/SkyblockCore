package me.illusion.skyblockcore.bungee.utilities;

import me.illusion.skyblockcore.bungee.SkyblockBungeePlugin;
import me.illusion.skyblockcore.shared.environment.ILogging;

import java.util.logging.Logger;

public class BungeeLoggingProvider implements ILogging {
    private static BungeeLoggingProvider instance;

    @Override
    public Logger getLogger() {
        return SkyblockBungeePlugin.instance.getLogger();
    }

    public static BungeeLoggingProvider get() {
        if (instance == null) {
            instance = new BungeeLoggingProvider();
        }

        return instance;
    }
}
