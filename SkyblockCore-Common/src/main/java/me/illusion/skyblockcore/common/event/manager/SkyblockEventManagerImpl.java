package me.illusion.skyblockcore.common.event.manager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import me.illusion.skyblockcore.common.event.SkyblockEvent;
import me.illusion.skyblockcore.common.event.listener.SkyblockEventListener;

/**
 * This class is a basic implementation of the SkyblockEventManager interface.
 */
public class SkyblockEventManagerImpl implements SkyblockEventManager {

    private final Set<SkyblockEventHandler<?>> handlers = ConcurrentHashMap.newKeySet(); // This could be better

    @Override
    public <T extends SkyblockEvent> void subscribe(Class<T> eventClass, SkyblockEventListener<T> listener) {
        System.out.println("Subscribing to event " + eventClass.getSimpleName());
        handlers.add(new SkyblockEventHandler<>(eventClass, listener));
    }

    @Override
    public <T extends SkyblockEvent> void callEvent(T event) {
        System.out.println("Calling event " + event.getClass().getSimpleName());

        for (SkyblockEventHandler<?> handler : handlers) {
            Class<?> eventClass = handler.getEventClass();

            if (!(eventClass.isAssignableFrom(event.getClass()))) {
                continue;
            }

            SkyblockEventHandler<T> castedHandler = (SkyblockEventHandler<T>) handler; // icky
            castedHandler.accept(event);
        }
    }

    /**
     * This is an internal class that represents a subscribed event handler.
     *
     * @param <T>
     */
    @Getter
    private static class SkyblockEventHandler<T extends SkyblockEvent> {

        private final Class<T> eventClass;
        private final SkyblockEventListener<T> listener;

        public SkyblockEventHandler(Class<T> eventClass, SkyblockEventListener<T> listener) {
            this.eventClass = eventClass;
            this.listener = listener;
        }

        public void accept(T event) {
            listener.onEvent(event);
        }
    }
}
