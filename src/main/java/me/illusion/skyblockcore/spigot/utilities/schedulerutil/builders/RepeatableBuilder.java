package me.illusion.skyblockcore.spigot.utilities.schedulerutil.builders;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleBuilderBase;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleData;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleThread;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.data.ScheduleTimestamp;

public class RepeatableBuilder extends ScheduleThread implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;


    RepeatableBuilder(ScheduleData data) {
        super(data);
        this.data = data;
    }

    public ScheduleTimestamp<ScheduleThread> during(long amount) {
        return new ScheduleTimestamp<>(new ScheduleThread(data), amount, data::setCancelIn);
    }
}
