package me.illusion.skyblockcore.common.scheduler;

import java.util.UUID;
import java.util.function.Consumer;
import me.illusion.skyblockcore.common.utilities.time.Time;

public interface SkyblockScheduler {

    boolean isCancelled(UUID taskId);

    void cancel(UUID taskId);


    default UUID scheduleOnce(ThreadContext context, Runnable task, Time delay) {
        return scheduleOnce(context, t -> task.run(), delay);
    }

    UUID scheduleOnce(ThreadContext context, Consumer<ScheduledTask> task, Time delay);

    default UUID scheduleRepeating(ThreadContext context, Runnable task, Time delay, Time period) {
        return scheduleRepeating(context, t -> task.run(), delay, period);
    }

    UUID scheduleRepeating(ThreadContext context, Consumer<ScheduledTask> task, Time delay, Time period);

}
