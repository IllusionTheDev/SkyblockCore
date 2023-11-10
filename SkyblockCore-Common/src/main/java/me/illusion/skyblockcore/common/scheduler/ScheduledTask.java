package me.illusion.skyblockcore.common.scheduler;

import java.util.UUID;

public interface ScheduledTask {

    boolean isCancelled();

    void cancel();

    UUID getTaskId();
}
