package me.illusion.skyblockcore.spigot.scheduler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import me.illusion.skyblockcore.common.scheduler.ScheduledTask;
import me.illusion.skyblockcore.common.scheduler.ScheduledTaskImpl;
import me.illusion.skyblockcore.common.scheduler.SkyblockScheduler;
import me.illusion.skyblockcore.common.scheduler.ThreadContext;
import me.illusion.skyblockcore.common.utilities.time.Time;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockBukkitScheduler implements SkyblockScheduler {

    private final Map<UUID, Integer> taskIds = new ConcurrentHashMap<>();
    private final Map<UUID, ScheduledTask> tasks = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;

    public SkyblockBukkitScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isCancelled(UUID taskId) {
        int bukkitTaskId = taskIds.getOrDefault(taskId, -1);

        if (bukkitTaskId == -1) {
            return true;
        }

        return Bukkit.getScheduler().isQueued(bukkitTaskId) || Bukkit.getScheduler().isCurrentlyRunning(bukkitTaskId);
    }

    @Override
    public void cancel(UUID taskId) {
        int bukkitTaskId = taskIds.remove(taskId);

        if (bukkitTaskId == -1) {
            return;
        }

        Bukkit.getScheduler().cancelTask(bukkitTaskId);
        tasks.remove(taskId);
    }

    @Override
    public UUID scheduleOnce(ThreadContext context, Consumer<ScheduledTask> task, Time delay) {
        UUID taskId = UUID.randomUUID();
        ScheduledTask scheduledTask = new ScheduledTaskImpl(taskId, this);

        Runnable runnable = () -> {
            task.accept(scheduledTask);
            tasks.remove(taskId);
            taskIds.remove(taskId);
        };

        int bukkitId;

        if (context == ThreadContext.ASYNC) {
            bukkitId = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay.asTicks()).getTaskId();
        } else {
            bukkitId = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay.asTicks()).getTaskId();
        }

        taskIds.put(taskId, bukkitId);
        tasks.put(taskId, scheduledTask);

        return taskId;
    }

    @Override
    public UUID scheduleRepeating(ThreadContext context, Consumer<ScheduledTask> task, Time delay, Time period) {
        UUID taskId = UUID.randomUUID();
        ScheduledTask scheduledTask = new ScheduledTaskImpl(taskId, this);

        int bukkitId;

        if (context == ThreadContext.ASYNC) {
            bukkitId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> task.accept(scheduledTask), delay.asTicks(), period.asTicks())
                .getTaskId();
        } else {
            bukkitId = Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(scheduledTask), delay.asTicks(), period.asTicks()).getTaskId();
        }

        taskIds.put(taskId, bukkitId);
        tasks.put(taskId, scheduledTask);

        return taskId;
    }
}
