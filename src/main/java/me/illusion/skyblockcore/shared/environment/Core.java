package me.illusion.skyblockcore.shared.environment;

// Keep this class name short!
public class Core {
    /**
     * Log
     * @param message message
     */
    public static void info(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        EnvironmentUtil.getLogger().info(builder.toString());
    }

    /**
     * Output a warning
     * @param message content
     */
    public static void warn(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        EnvironmentUtil.getLogger().warning(builder.toString());
    }

    /**
     * Output an error (maybe something else)
     * @param message message content
     */
    public static void severe(Object... message) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : message) {
            builder.append(obj);
        }

        EnvironmentUtil.getLogger().severe(builder.toString());
    }
}
