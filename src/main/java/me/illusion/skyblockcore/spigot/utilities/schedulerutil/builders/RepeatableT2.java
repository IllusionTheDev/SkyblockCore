package me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleBuilderBase;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleData;

public class RepeatableT2 implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;

    RepeatableT2(ScheduleData data) {
        this.data = data;
    }

    public RepeatableBuilder run(Runnable runnable) {
        data.setRunnable(runnable);
        return new RepeatableBuilder(data);
    }
}
