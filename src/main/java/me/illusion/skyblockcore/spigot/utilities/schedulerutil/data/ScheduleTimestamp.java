package me.illusion.skyblockcore.spigot.utilities.schedulerutil.data;

import java.util.function.Consumer;

public class ScheduleTimestamp<T extends ScheduleBuilderBase> {

    private final T builder;
    private final long time;
    private final Consumer<Long> execute;

    public ScheduleTimestamp(T builder, long time, Consumer<Long> execute) {
        this.builder = builder;
        this.time = time;
        this.execute = execute;
    }

    public T ticks() {
        execute.accept(time);
        return builder;
    }

    public T seconds() {
        execute.accept(time * 20);
        return builder;
    }

    public T minutes() {
        execute.accept(time * 60 * 20);
        return builder;
    }

    public T hours() {
        execute.accept(time * 60 * 60 * 20);
        return builder;
    }

}
