package me.illusion.skyblockcore.shared.environment;

import me.illusion.skyblockcore.shared.utilities.SoftwareDetectionUtil;
import me.illusion.skyblockcore.spigot.SkyblockPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
        if (getServerType() == ServerType.SPIGOT) {
            return JavaPlugin.getPlugin(SkyblockPlugin.class).getLogger();
        } else {
            return ProxyServer.getInstance().getLogger();
        }
    }

    public static ServerType getServerType() {
        return SoftwareDetectionUtil.isBukkit() ? ServerType.SPIGOT : ServerType.BUNGEE;
    }
}
