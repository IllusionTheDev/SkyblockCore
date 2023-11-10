package me.illusion.skyblockcore.common.scheduler;

import java.util.UUID;

public class ScheduledTaskImpl implements ScheduledTask {

    private final UUID taskId;
    private final SkyblockScheduler scheduler;

    public ScheduledTaskImpl(UUID taskId, SkyblockScheduler scheduler) {
        this.taskId = taskId;
        this.scheduler = scheduler;
    }

    @Override
    public boolean isCancelled() {
        return scheduler.isCancelled(taskId);
    }

    @Override
    public void cancel() {
        scheduler.cancel(taskId);
    }

    @Override
    public UUID getTaskId() {
        return taskId;
    }
}
