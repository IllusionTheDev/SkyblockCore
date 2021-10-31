package me.illusion.skyblockcore.shared.utilities;

public final class SoftwareDetectionUtil {

    private static boolean ranDetections;

    private static boolean bukkit;

    private SoftwareDetectionUtil() {

    }

    private static void detect() {
        if (ranDetections)
            return;

        try {
            Class.forName("org.bukkit.Bukkit");
            bukkit = true;
        } catch (ClassNotFoundException error) {
            bukkit = false;
        }

        ranDetections = true;
    }

    public static boolean isBukkit() {
        detect();
        return bukkit;
    }

    public static boolean isProxy() {
        return !isBukkit();
    }

    public static boolean isMainThread() {
        return isProxy();
    }
}
