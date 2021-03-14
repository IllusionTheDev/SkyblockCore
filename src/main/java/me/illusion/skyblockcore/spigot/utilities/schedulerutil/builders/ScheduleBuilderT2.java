package me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleBuilderBase;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleData;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleThread;

public class ScheduleBuilderT2 implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;

    ScheduleBuilderT2(ScheduleData data) {
        this.data = data;
    }

    public ScheduleThread run(Runnable runnable) {
        data.setRunnable(runnable);
        return new ScheduleThread(data);
    }

}
