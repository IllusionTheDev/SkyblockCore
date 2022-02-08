package me.illusion.skyblockcore.shared.environment;

import me.illusion.skyblockcore.shared.utilities.SoftwareDetectionUtil;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;

import java.util.logging.Logger;

public class EnvironmentUtil {
    private static Logger logger;

    /**
     * Set global logger
     * **AWARE: THIS MUST BE INVOKED IMMEDIATELY AS SOON AS THE PLUGIN ENABLES**
     * @param logger0 global logger
     */
    public static void setLogger(Logger logger0) {
        if (logger != null) {
            logger.severe("Already set logger! This may caused by reloading at runtime. Otherwise this is a Bug.");
        }

        logger = logger0;
    }

    public static Logger getLogger() {
        if (logger == null) {
            throw new IllegalStateException("Requested for logger before it's set");
        }

        return logger;
    }

    public static ServerType getServerType() {
        return SoftwareDetectionUtil.isBukkit() ? ServerType.SPIGOT : ServerType.BUNGEE;
    }
}
