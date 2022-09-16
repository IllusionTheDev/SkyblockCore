package me.illusion.skyblockcore.spigot.utilities.concurrent;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public final class MainThreadExecutor implements Executor {

    public static final MainThreadExecutor MAIN_THREAD_EXECUTOR = new MainThreadExecutor();
    private static JavaPlugin plugin;

    private MainThreadExecutor() {
    }

    public static void init(@NotNull JavaPlugin main) {
        MainThreadExecutor.plugin = main;
    }

    @Override
    public void execute(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        Runnable task = () -> {
            runnable.run();
            latch.countDown();
        };

        Bukkit.getScheduler().runTask(plugin, task);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace(); // if this happens, something is very very very very wrong
        }
    }
}
