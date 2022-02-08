package me.illusion.skyblockcore.spigot.utilities;

import me.illusion.skyblockcore.shared.environment.ILogging;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class LoggingProvider implements ILogging {
    private static LoggingProvider instance;

    @Override
    public Logger getLogger() {
        return JavaPlugin.getPlugin(SkyblockPlugin.class).getLogger();
    }

    public static LoggingProvider get() {
        if (instance == null) instance = new LoggingProvider();
        return instance;
    }
}
