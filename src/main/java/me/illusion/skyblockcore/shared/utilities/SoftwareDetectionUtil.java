package me.illusion.skyblockcore.shared.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class SoftwareDetectionUtil {

    private static boolean ranDetections;
    private static boolean bukkit;

    private static Method checkSync;

    private SoftwareDetectionUtil() {

    }

    private static void detect() {
        if (ranDetections)
            return;

        try {
            Class<?> clazz = Class.forName("org.bukkit.Bukkit");
            checkSync = clazz.getMethod("isPrimaryThread");
            bukkit = true;
        } catch (ClassNotFoundException | NoSuchMethodException error) {
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
        try {
            return isBukkit() ? (boolean) checkSync.invoke(null) : Thread.currentThread().getName().equals("Server thread");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }
}
