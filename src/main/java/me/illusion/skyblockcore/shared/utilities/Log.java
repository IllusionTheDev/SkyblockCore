package me.illusion.skyblockcore.shared.utilities;

import me.illusion.skyblockcore.shared.environment.EnvironmentUtil;

public class Log {
    /**
     * Log
     *
     * @param message message
     */
    @Deprecated
    public static void info(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        EnvironmentUtil.getLogger().info(builder.toString());
    }

    /**
     * Output a warning
     *
     * @param message content
     */
    @Deprecated
    public static void warn(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        EnvironmentUtil.getLogger().warning(builder.toString());
    }

    /**
     * Output an error (maybe something else)
     *
     * @param message message content
     */
    @Deprecated
    public static void severe(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        EnvironmentUtil.getLogger().severe(builder.toString());
    }
}
