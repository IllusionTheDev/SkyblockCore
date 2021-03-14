package me.illusion.skyblockcore.spigot.utilities.schedulerutil.data;

import lombok.Getter;
import me.illusion.skyblockcore.spigot.utilities.schedulerutil.ScheduledTask;

public class ScheduleThread implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;

    public ScheduleThread(ScheduleData data) {
        this.data = data;
    }

    public ScheduledTask sync() {
        data.setSync(true);
        return new ScheduledTask(data.getPlugin(), data);
    }

    public ScheduledTask async() {
        data.setSync(false);
        return new ScheduledTask(data.getPlugin(), data);
    }


}
