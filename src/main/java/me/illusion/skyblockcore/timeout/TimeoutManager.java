package me.illusion.skyblockcore.timeout;

import java.util.HashMap;
import java.util.Map;

public class TimeoutManager {

    private final Map<String, Timeout<?>> timeouts = new HashMap<>();

    public <T> void register(String name, int duration, Class<T> clazz) {
        timeouts.put(name, new Timeout<T>(duration, clazz));
    }

    public <T> Timeout<T> getTimeout(String name) {
        return (Timeout<T>) timeouts.get(name);
    }

    public <T> boolean isInTimeout(String name, Object object) {
        Timeout<T> timeout = (Timeout<T>) timeouts.get(name);

        if (timeout == null)
            return false;

        if (timeout.getTypeClass() != object.getClass())
            return false;

        return timeout.isInTimeout((T) object);
    }
}
