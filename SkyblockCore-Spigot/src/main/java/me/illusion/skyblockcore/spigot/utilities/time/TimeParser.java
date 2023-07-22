package me.illusion.skyblockcore.spigot.utilities.time;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import me.illusion.cosmos.utilities.time.Time;

public class TimeParser {

    private static final Map<TimeUnit, List<String>> UNIT_ALIASES = Map.of(
        TimeUnit.SECONDS, List.of("s", "sec", "secs", "second", "seconds"),
        TimeUnit.MINUTES, List.of("m", "min", "mins", "minute", "minutes"),
        TimeUnit.HOURS, List.of("h", "hr", "hrs", "hour", "hours"),
        TimeUnit.DAYS, List.of("d", "day", "days")
    );

    public static Time parse(String input) {
        String[] split = input.split(" ");

        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid time format");
        }

        int time = Integer.parseInt(split[0]);
        TimeUnit unit = parseUnit(split[1]);

        return new Time(time, unit);
    }

    private static TimeUnit parseUnit(String input) {
        for (Map.Entry<TimeUnit, List<String>> entry : UNIT_ALIASES.entrySet()) {
            if (entry.getValue().contains(input)) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Invalid time unit");
    }
}
