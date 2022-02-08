package me.illusion.skyblockcore.shared.environment;

import java.util.logging.Logger;

public interface ILogging {
    Logger getLogger();

    /**
     * Info
     * @param message
     */
    default void info(Object... message) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : message) builder.append(obj);
        getLogger().info(builder.toString());
    }

    /**
     * Warn
     * @param message
     */
    default void warn(Object... message) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : message) builder.append(obj);
        getLogger().warning(builder.toString());
    }

    /**
     * Severe
     * @param message
     */
    default void severe(Object... message) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : message) builder.append(obj);
        getLogger().severe(builder.toString());
    }
}
