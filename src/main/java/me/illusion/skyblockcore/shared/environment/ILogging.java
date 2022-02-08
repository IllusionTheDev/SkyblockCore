package me.illusion.skyblockcore.shared.environment;

public interface Log {
    void info(Object... message);
    void warn(Object... message);
    void severe(Object... message);
}
