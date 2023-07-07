package me.illusion.skyblockcore.spigot.utilities.command.language.unit;

public enum MinecraftTime {
    TICKS(1),
    SECONDS(20),
    MINUTES(1200),
    HOURS(72000),
    DAYS(1728000),
    WEEKS(12096000),
    MONTHS(51840000),
    YEARS(622080000);

    private final int multiplier;

    MinecraftTime(int multiplier) {
        this.multiplier = multiplier;
    }

    public long asTicks() {
        return multiplier;
    }
}
